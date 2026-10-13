/**
 * Everything project-specific about the coverage tooling: where the sources and reports live,
 * which files count, how the backend is started and where its JaCoCo agent listens. The collectors
 * (coverage.ts, backend-coverage.ts) and the reader (coverage-gaps.ts) take all of it from here.
 *
 * Kept free of dependencies, so coverage-gaps.ts loads neither monocart nor the TypeScript compiler.
 *
 * Every path handed to git or compared with a report entry is repo-relative with forward slashes,
 * exactly as `git diff --name-only` prints it.
 */

import { fileURLToPath } from 'node:url';

export const REPO_ROOT = fileURLToPath(new URL('../', import.meta.url));

// --- Frontend -----------------------------------------------------------------------------------

export const FRONTEND_SOURCE_DIR = 'frontend/src/';

export const FRONTEND_DEV_SERVER_COMMAND = 'cd frontend && npm start';

export const FRONTEND_REPORT_DIR = fileURLToPath(new URL('./coverage/frontend/', import.meta.url));
export const FRONTEND_LCOV = `${FRONTEND_REPORT_DIR}lcov.info`;

/**
 * The frontend's own sources. Specs never reach the browser, and per-stage environment files
 * only replace environment.ts in builds other than the dev server's. Templates are out too: they
 * come out of the source map with line numbers from the compiled render function, which point
 * nowhere useful in the .html file.
 */
export function isFrontendSource(path: string): boolean {
  return (
    path.startsWith(FRONTEND_SOURCE_DIR) &&
    path.endsWith('.ts') &&
    !path.endsWith('.spec.ts') &&
    !/\/environments\/environment\.[^/]+\.ts$/.test(path)
  );
}

// --- Backend (optional) -------------------------------------------------------------------------
// Without a JVM backend, leave these as they are: no agent answers on the port, and every run
// measures the frontend alone.

export const BACKEND_DIR = fileURLToPath(new URL('../backend/', import.meta.url));

export const BACKEND_MAVEN_PROFILE = 'e2e-coverage';

export const BACKEND_START_COMMAND = `cd backend && ./mvnw -P${BACKEND_MAVEN_PROFILE} jacoco:prepare-agent spring-boot:run`;

// Not JaCoCo's default 6300: on this machine another project's backend (:8080) already runs a JaCoCo
// agent on 6300, and a run would reset and dump *its* counters. The profile needs <port>6301</port>.
export const JACOCO_AGENT_PORT = 6301;

export const BACKEND_SOURCE_ROOT = 'backend/src/main/java/';

/** Must match the jacoco-maven-plugin's outputDirectory in the profile. */
export const BACKEND_REPORT_DIR = `${BACKEND_DIR}target/site/jacoco-e2e/`;
export const BACKEND_JACOCO_XML = `${BACKEND_REPORT_DIR}jacoco.xml`;
/**
 * Class names whose execution data did not match target/classes at report time, one per line.
 * Written by backend-coverage.ts, read by coverage-gaps.ts.
 */
export const BACKEND_MISMATCH_FILE = `${BACKEND_REPORT_DIR}class-mismatch.txt`;

export function isBackendSource(path: string): boolean {
  return path.startsWith(BACKEND_SOURCE_ROOT) && path.endsWith('.java');
}
