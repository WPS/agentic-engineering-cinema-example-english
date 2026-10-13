/**
 * The one place that knows how this app logs a user in. shot.ts and the specs import
 * their auth from here and from nowhere else, so swapping the login — a different form, an identity
 * provider, a magic link — is a change to this file alone.
 *
 * Plain node runs this file as well as the Playwright runner (see flows.ts), so `Page` comes in
 * as `import type` and waits use `locator.waitFor()`, never `expect()`.
 *
 * KinoSoft has no authentication, so this is a no-op login that only waits for the app shell. The
 * shape of the four exports is what the rest of the harness relies on and stays.
 */

import type { Locator, Page } from '@playwright/test';

export interface TestUser {
  username: string;
  password: string;
}

/**
 * The account every authenticated shot and the smoke spec use unless E2E_USER / E2E_PASSWORD say
 * otherwise.
 */
export const DEFAULT_USER: TestUser = {
  // KinoSoft has no login: every visitor is anonymous, so the "user" is a placeholder that only
  // names the cached session file. E2E_USER / E2E_PASSWORD are still honoured for the harness's sake.
  username: process.env.E2E_USER ?? 'anonymous',
  password: process.env.E2E_PASSWORD ?? '',
};

/** Long enough for a cold dev server's first render, short enough to fail a dead stack fast. */
export const LOGIN_TIMEOUT = 20_000;

/**
 * KinoSoft has no signed-in and no signed-out header state. The one marker is the app shell
 * itself: the navbar's page title, the only h1 on every route. Waiting for it waits for Angular to
 * have rendered, which is all "signed in" can mean here.
 */
function appShell(page: Page): Locator {
  return page.locator('app-navbar h1');
}

/**
 * No-op login: KinoSoft has no authentication (no guards, /api open). Opens the home page and
 * returns once the app shell has rendered, so callers keep their "log in, then look" order.
 */
export async function login(page: Page, user: TestUser): Promise<void> {
  await page.goto('/');
  await appShell(page)
    .waitFor({ state: 'visible', timeout: LOGIN_TIMEOUT })
    .catch(() => {
      throw new Error(`the app shell never rendered for ${user.username} — is the dev server at the baseURL this run points at?`);
    });
}

/**
 * Always true once the app shell has rendered: there is no signed-out state. Still waits for the
 * marker rather than sampling the DOM, so a slow first render is not mistaken for "no app".
 */
export async function isLoggedIn(page: Page): Promise<boolean> {
  return appShell(page)
    .waitFor({ state: 'visible', timeout: LOGIN_TIMEOUT })
    .then(() => true)
    .catch(() => false);
}

/** The file-name stem of a user's cached session under .auth/: one file per login, never shared. */
export function sessionSlug(user: TestUser): string {
  return user.username.replace(/[^a-zA-Z0-9]+/g, '-');
}
