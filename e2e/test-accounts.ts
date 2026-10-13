/**
 * The suite's test accounts and their reset before and after every run.
 *
 * KinoSoft has no login and therefore no accounts: TEST_ACCOUNTS is empty and the reset is a no-op
 * that says so once. The database is recreated on every backend start (docs/Datenbank.md); that,
 * not a reset endpoint, is how the local stack gets back to a clean state.
 *
 * Plain node, like flows.ts: global-setup.ts, global-teardown.ts and shot.ts import from here.
 */

import type { FullConfig } from '@playwright/test';
import type { TestUser } from './login.ts';

/** Every account the suite resets: none, KinoSoft has no accounts. */
export const TEST_ACCOUNTS: readonly TestUser[] = [];

let announcedNoop = false;

/** No-op: there is nothing to reset. Announces that once per run. */
export async function resetTestAccounts(_baseUrl: string): Promise<void> {
  if (announcedNoop) return;
  console.warn('this app has no accounts to reset');
  announcedNoop = true;
}

/** The target the run points at, as playwright.config.ts resolved it. */
export function baseUrlOf(config: FullConfig): string {
  const baseUrl = config.projects[0]?.use.baseURL;
  if (!baseUrl) throw new Error('playwright.config.ts sets no baseURL');
  return baseUrl;
}
