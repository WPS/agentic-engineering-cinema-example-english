/**
 * Browser flows shared by the specs and throwaway scripts: one copy of every step that drives one
 * of the app's forms, so a renamed field breaks in one place instead of five. (KinoSoft has no
 * seeder: its data comes from the backend's start, see docs/Datenbank.md.)
 *
 * These only drive the forms — they assert nothing. A spec that calls a step here still tests the
 * app's own flow; the step just types into it. What a step may throw on is a precondition the
 * caller cannot see, such as a record that already exists under the name asked for. Assertions
 * stay in the specs, and a step that fills a form deliberately does not submit it when a spec
 * wants to assert something between filling and submitting.
 *
 * Two runtimes share this file: the specs go through the Playwright runner, scripts in e2e/tmp/
 * through plain `node`, which strips types natively (Node >= 23.6) without checking them. Hence:
 *
 *   - Nothing here may import a *value* from @playwright/test. `Page` comes in as `import type`,
 *     which is erased before node sees it, and waits use `locator.waitFor()` rather than `expect()`.
 *   - `npm run typecheck` is the only thing that checks this file — node will happily run a type
 *     error. Run it after every change in here.
 *
 * The login itself lives in login.ts, which follows the same rules.
 */

import type { Page } from '@playwright/test';

/** How long a step waits for the app to answer; generous, because a cold dev server is slow. */
export const STEP_TIMEOUT = 30_000;

/**
 * Opens the program of the day the page shows first (today) and starts the ticket sale for its
 * first screening: the first film card's first showtime button. Leaves the page on
 * /ticket-sale/<screeningId> once the URL says so and the navbar title has rendered. Language-neutral
 * (German "Kartenverkauf", English "Buy tickets"), so it works whichever language is stored. Throws
 * if the day has no screening — the backend generates the program for the current week on start.
 */
export async function openFirstScreening(page: Page): Promise<void> {
  await page.goto('/program');
  const firstScreening = page.locator('app-program-entry .card-actions button').first();
  await firstScreening.waitFor({ state: 'visible', timeout: STEP_TIMEOUT });
  await firstScreening.click();
  await page.waitForURL(/\/ticket-sale\/[^/?#]+/, { timeout: STEP_TIMEOUT });
  await page.locator('app-ticket-sale-page').waitFor({ state: 'visible', timeout: STEP_TIMEOUT });
}
