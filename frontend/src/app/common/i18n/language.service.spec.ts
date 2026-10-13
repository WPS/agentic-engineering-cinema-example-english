import {TestBed} from '@angular/core/testing';
import {DOCUMENT} from '@angular/common';
import {LanguageService} from './language.service';
import {MessageKey} from './messages';

const STORAGE_KEY = 'kinosoft.language';

describe('LanguageService', () => {
  let service: LanguageService;

  function createService(): LanguageService {
    TestBed.configureTestingModule({});
    return TestBed.inject(LanguageService);
  }

  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
    document.documentElement.lang = '';
  });

  it('defaults to German', () => {
    service = createService();
    expect(service.language()).toBe('de');
    expect(service.localeId()).toBe('de');
  });

  it('switches the language with setLanguage', () => {
    service = createService();
    service.setLanguage('en');
    expect(service.language()).toBe('en');
    expect(service.localeId()).toBe('en-US');
  });

  it('translates a key in the current language', () => {
    service = createService();
    expect(service.translate('common.menu')).toBe('Menü');
    service.setLanguage('en');
    expect(service.translate('common.menu')).toBe('Menu');
  });

  it('returns the key itself for an unknown key', () => {
    service = createService();
    expect(service.translate('does.not.exist' as MessageKey)).toBe('does.not.exist');
  });

  it('fills placeholders and leaves unknown placeholders as they are', () => {
    service = createService();
    // Placeholder substitution is generic; use an unknown key so the key itself is the template.
    const template = 'Row {row}, seat {seat}, {unknown}' as MessageKey;
    expect(service.translate(template, {row: 3, seat: 'A'})).toBe('Row 3, seat A, {unknown}');
  });

  it('sets the lang attribute of the document', () => {
    service = createService();
    const doc = TestBed.inject(DOCUMENT);
    TestBed.tick();
    expect(doc.documentElement.lang).toBe('de');

    service.setLanguage('en');
    TestBed.tick();
    expect(doc.documentElement.lang).toBe('en');
  });

  it('persists the chosen language in localStorage', () => {
    service = createService();
    service.setLanguage('en');
    TestBed.tick();
    expect(localStorage.getItem(STORAGE_KEY)).toBe('en');
  });

  it('restores a stored language', () => {
    localStorage.setItem(STORAGE_KEY, 'en');
    service = createService();
    expect(service.language()).toBe('en');
  });

  it('ignores an invalid stored language', () => {
    localStorage.setItem(STORAGE_KEY, 'fr');
    service = createService();
    expect(service.language()).toBe('de');
  });
});
