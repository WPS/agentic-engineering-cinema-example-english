import { test, expect } from '../fixtures.ts';
import { DEFAULT_USER, isLoggedIn, login } from '../login.ts';

/**
 * The one spec that must stay green on every stack, the deployed one included: the home page
 * renders. KinoSoft has no login, so login() and isLoggedIn() (login.ts) only wait for the app
 * shell; the calls stay so that a login added later is exercised here without touching the spec.
 */
test.describe('Smoke test', () => {
  test('home loads and the app shell renders', async ({ page }) => {
    await page.goto('/');
    // '/' redirects to /program, whose navbar renders the page title as the only h1. German is the
    // default language, and a fresh browser context has no stored choice.
    await expect(page.getByRole('heading', { level: 1, name: 'Programm' })).toBeVisible();

    await login(page, DEFAULT_USER);

    // Back to the home page, which is where login.ts reads the header state from.
    await page.goto('/');
    expect(await isLoggedIn(page)).toBe(true);
  });
});
