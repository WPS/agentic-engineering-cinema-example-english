export type Language = 'de' | 'en';

export const LANGUAGES: readonly Language[] = ['de', 'en'];

export const DEFAULT_LANGUAGE: Language = 'de';

/** Angular locale id per language, used for date formatting. */
export const LOCALE_IDS: Record<Language, string> = {de: 'de', en: 'en-US'};
