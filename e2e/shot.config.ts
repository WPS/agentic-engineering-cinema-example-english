import type { ShotConfig } from './shot-sensors.ts';

/**
 * The sensor settings of the screenshot harness — the human's half. The agent's flags (`--checks`,
 * `--skip`, `--detail`) can narrow what runs, never widen it: a sensor set to `false` here stays off.
 * Every field and its default is documented on the interfaces in shot-sensors.ts.
 *
 * KinoSoft (decided by the human, 2026-10-01): all five sensors on. The calendar bar highlights
 * today and the backend regenerates the program for the current ISO week on every start, so the
 * diff masks it; freezeTime would not help, the data follows the real date. a11y stays complete:
 * its findings are real and wanted.
 */
const config: ShotConfig = {
  checks: {
    diff: { mask: ['app-calendar'] },
  },
};

export default config;
