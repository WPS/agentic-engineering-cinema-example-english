import {DOCUMENT} from '@angular/common';
import {effect, inject, Injectable, signal} from '@angular/core';
import {DEFAULT_LANGUAGE, Language, LANGUAGES, LOCALE_IDS} from './language';
import {MessageKey, MESSAGES} from './messages';

const STORAGE_KEY = 'kinosoft.language';

export type MessageParams = Record<string, string | number>;

/**
 * Holds the current UI language and translates messages. The language can be switched at runtime;
 * everything reading `language()` (templates, computed signals, the pipes) updates immediately.
 */
@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private readonly document = inject(DOCUMENT);

  private readonly current = signal<Language>(this.restore());

  readonly language = this.current.asReadonly();

  constructor() {
    effect(() => {
      const language = this.current();
      this.document.documentElement.lang = language;
      this.persist(language);
    });
  }

  setLanguage(language: Language): void {
    this.current.set(language);
  }

  localeId(): string {
    return LOCALE_IDS[this.current()];
  }

  translate(key: MessageKey, params?: MessageParams): string {
    const message = MESSAGES[this.current()][key] ?? key;
    if (!params) {
      return message;
    }
    return message.replace(/\{(\w+)\}/g, (placeholder, name: string) =>
      name in params ? String(params[name]) : placeholder);
  }

  private restore(): Language {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored && (LANGUAGES as readonly string[]).includes(stored)) {
        return stored as Language;
      }
    } catch {
      // Storage unavailable (private mode, tests): fall back to the default.
    }
    return DEFAULT_LANGUAGE;
  }

  private persist(language: Language): void {
    try {
      localStorage.setItem(STORAGE_KEY, language);
    } catch {
      // Storage unavailable: the choice just isn't remembered.
    }
  }
}
