import { test, expect } from '../fixtures.ts';
import type { Page } from '@playwright/test';

/**
 * The navbar's DE/EN switch: German is the default, a click switches the UI at runtime, the choice
 * survives a reload (localStorage `kinosoft.language`) and is mirrored into `<html lang>`. Every
 * test starts in a fresh context, so no stored choice leaks in from another spec.
 */

/** The switch is a labelled group whose label is itself translated ("Sprache" / "Language"). */
function languageSwitch(page: Page) {
  return page.getByRole('group', { name: /^(Sprache|Language)$/ });
}

function languageButton(page: Page, label: 'DE' | 'EN') {
  return languageSwitch(page).getByRole('button', { name: label, exact: true });
}

/** The navbar renders the page title as the only h1 on every route. */
function pageTitle(page: Page, name: string) {
  return page.getByRole('heading', { level: 1, name, exact: true });
}

test.describe('Language switch', () => {
  test('switches to English, keeps it across a reload, and switches back', async ({ page }) => {
    await page.goto('/');
    await expect(pageTitle(page, 'Programm')).toBeVisible();
    await expect(languageButton(page, 'DE')).toHaveAttribute('aria-pressed', 'true');
    await expect(page.locator('html')).toHaveAttribute('lang', 'de');

    await languageButton(page, 'EN').click();
    await expect(pageTitle(page, 'Program')).toBeVisible();
    await expect(languageButton(page, 'EN')).toHaveAttribute('aria-pressed', 'true');
    await expect(languageButton(page, 'DE')).toHaveAttribute('aria-pressed', 'false');

    await page.reload();
    await expect(pageTitle(page, 'Program')).toBeVisible();
    await expect(page.locator('html')).toHaveAttribute('lang', 'en');

    await languageButton(page, 'DE').click();
    await expect(pageTitle(page, 'Programm')).toBeVisible();
    await expect(languageButton(page, 'DE')).toHaveAttribute('aria-pressed', 'true');
    await expect(page.locator('html')).toHaveAttribute('lang', 'de');
  });

  test('the old German URL /programm redirects to /program', async ({ page }) => {
    await page.goto('/programm');
    await expect(page).toHaveURL(/\/program(\?.*)?$/);
    await expect(pageTitle(page, 'Programm')).toBeVisible();
  });
});
