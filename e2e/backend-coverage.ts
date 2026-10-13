/**
 * Which backend code an e2e run executes, from the JaCoCo agent of a JVM backend started with the
 * coverage Maven profile (BACKEND_MAVEN_PROFILE and BACKEND_START_COMMAND in coverage-paths.ts).
 * Optional: without a JVM backend, or without the agent, every run measures the frontend alone
 * and says so.
 *
 * The agent keeps its counters in the running JVM and hands them out over TCP on
 * localhost:JACOCO_AGENT_PORT, so a running backend can be measured without a restart. The run
 * zeroes them once the project's global setup has run (e.g. a test-data reset), which drops
 * whatever the backend executed before, its own startup included, and fetches them before the
 * project's global teardown runs. Code that only runs at startup (configuration, dependency
 * wiring) therefore shows as uncovered, and the harness's own data resets do not count.
 *
 * Anything short of a local backend with a working agent is not an error: the run then reports
 * the frontend alone and says why.
 *
 * The profile, for the backend's pom.xml. The agent is attached through prepare-agent on the
 * command line rather than bound to a phase: bound to a phase, it would run in spring-boot:run's
 * forked lifecycle, whose jacocoAgent property the run goal never sees. Output tcpserver instead
 * of writing the counters at JVM exit is what allows measuring a running backend.
 *
 *   <profile>
 *     <id>e2e-coverage</id>
 *     <build>
 *       <plugins>
 *         <plugin>
 *           <groupId>org.jacoco</groupId>
 *           <artifactId>jacoco-maven-plugin</artifactId>
 *           <version>0.8.15</version>
 *           <configuration>
 *             <propertyName>jacocoAgent</propertyName>
 *             <output>tcpserver</output>
 *             <address>localhost</address>
 *             <includes><include>com/example/**</include></includes>
 *             <destFile>${project.build.directory}/jacoco-e2e.exec</destFile>
 *             <dataFile>${project.build.directory}/jacoco-e2e.exec</dataFile>
 *             <outputDirectory>${project.reporting.outputDirectory}/jacoco-e2e</outputDirectory>
 *           </configuration>
 *         </plugin>
 *         <plugin>
 *           <groupId>org.springframework.boot</groupId>
 *           <artifactId>spring-boot-maven-plugin</artifactId>
 *           <configuration><jvmArguments>${jacocoAgent}</jvmArguments></configuration>
 *         </plugin>
 *       </plugins>
 *     </build>
 *   </profile>
 *
 * Profile not yet added to backend/pom.xml; the human adds it (with <port>6301</port> inside the
 * jacoco-maven-plugin <configuration>, see JACOCO_AGENT_PORT in coverage-paths.ts, and the backend's
 * base package in <includes>).
 */

import { spawnSync } from 'node:child_process';
import { createConnection } from 'node:net';
import { rmSync, writeFileSync } from 'node:fs';
import { pathToFileURL } from 'node:url';
import {
  BACKEND_DIR,
  BACKEND_MAVEN_PROFILE,
  BACKEND_MISMATCH_FILE,
  BACKEND_REPORT_DIR,
  BACKEND_START_COMMAND,
  JACOCO_AGENT_PORT,
} from './coverage-paths.ts';

/**
 * Setup and teardown run in the same runner process. Without a successful reset, a dump would
 * report everything the backend executed since it started.
 */
const RESET_MARKER = 'E2E_BACKEND_COVERAGE_RESET';

/** Against a remote stack, the agent on this machine belongs to a backend the run never calls. */
function isLocal(baseUrl: string): boolean {
  return ['localhost', '127.0.0.1', '[::1]'].includes(new URL(baseUrl).hostname);
}

function agentResponds(): Promise<boolean> {
  return new Promise((resolve) => {
    const socket = createConnection({ host: 'localhost', port: JACOCO_AGENT_PORT, timeout: 1000 });
    socket.once('connect', () => {
      socket.destroy();
      resolve(true);
    });
    socket.once('error', () => resolve(false));
    socket.once('timeout', () => {
      socket.destroy();
      resolve(false);
    });
  });
}

/** Returns Maven's output. Its own JVM warnings land on stderr on every call, so that is shown only on failure. */
function maven(...args: string[]): string {
  const windows = process.platform === 'win32';
  const result = spawnSync(windows ? 'mvnw.cmd' : './mvnw', [`-P${BACKEND_MAVEN_PROFILE}`, ...args], {
    cwd: BACKEND_DIR,
    encoding: 'utf8',
    shell: windows,
  });
  if (result.error) throw new Error(`mvnw in ${BACKEND_DIR} could not start: ${result.error.message}`);
  if (result.status !== 0) throw new Error(`mvnw ${args.join(' ')} failed:\n${result.stdout}${result.stderr}`);
  return result.stdout;
}

function frontendOnly(reason: string): void {
  console.log(`Backend coverage skipped, this run measures the frontend only: ${reason}`);
}

export async function resetBackendCoverage(baseUrl: string | undefined): Promise<void> {
  // A report left over from an earlier run must not pass for this one's.
  rmSync(BACKEND_REPORT_DIR, { recursive: true, force: true });
  delete process.env[RESET_MARKER];

  if (!baseUrl) return frontendOnly('playwright.config.ts sets no baseURL.');
  if (!isLocal(baseUrl)) return frontendOnly(`${baseUrl} is not a local stack.`);
  if (!(await agentResponds())) {
    return frontendOnly(
      `no JaCoCo agent on localhost:${JACOCO_AGENT_PORT}. Start the backend with ${BACKEND_START_COMMAND}, see e2e/backend-coverage.ts.`,
    );
  }
  try {
    maven('-q', 'jacoco:dump', '-Djacoco.dump=false', '-Djacoco.reset=true');
    process.env[RESET_MARKER] = '1';
  } catch (failure) {
    frontendOnly(`resetting the agent failed. ${String(failure)}`);
  }
}

/**
 * Dumps the counters over TCP and turns them into jacoco.xml (read by coverage-gaps.ts), an HTML
 * report and a CSV.
 *
 * JaCoCo matches the counters to target/classes as they are now. When something recompiled the
 * backend after it started (a parallel session, an IDE build, a test run in another terminal), the
 * changed classes no longer match and report 0 %. JaCoCo only warns, so the names are written
 * down for coverage-gaps.ts.
 */
export async function writeBackendCoverageReport(): Promise<void> {
  if (process.env[RESET_MARKER] !== '1') return;
  try {
    const output = maven('jacoco:dump', '-Djacoco.append=false', 'jacoco:report');
    const mismatched = [...output.matchAll(/Execution data for class (\S+) does not match/g)].map((m) => m[1]);
    if (mismatched.length > 0) {
      writeFileSync(BACKEND_MISMATCH_FILE, `${mismatched.join('\n')}\n`);
      console.warn(
        `[JaCoCo] ${mismatched.length} backend classes were recompiled after the backend started and report ` +
          `0 %. Restart the backend (${BACKEND_START_COMMAND}) and re-run for true numbers.`,
      );
    }
    console.log(`[JaCoCo] backend report: ${pathToFileURL(`${BACKEND_REPORT_DIR}index.html`).href}`);
  } catch (failure) {
    rmSync(BACKEND_REPORT_DIR, { recursive: true, force: true });
    console.warn(`[JaCoCo] no backend report: ${String(failure)}`);
  }
}
