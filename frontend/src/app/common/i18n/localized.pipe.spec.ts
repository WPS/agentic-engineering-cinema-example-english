import {TestBed} from '@angular/core/testing';
import {LocalizedPipe} from './localized.pipe';
import {LanguageService} from './language.service';
import {LocalizedText} from './localized-text';

describe('LocalizedPipe', () => {
  const hall: LocalizedText = {de: 'großer Saal', en: 'Large Hall'};

  let pipe: LocalizedPipe;
  let languageService: LanguageService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    languageService = TestBed.inject(LanguageService);
    pipe = TestBed.runInInjectionContext(() => new LocalizedPipe());
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('picks the German text by default', () => {
    expect(pipe.transform(hall)).toBe('großer Saal');
  });

  it('picks the English text after a language switch', () => {
    languageService.setLanguage('en');
    expect(pipe.transform(hall)).toBe('Large Hall');
  });

  it('falls back to German when the text for the current language is missing', () => {
    languageService.setLanguage('en');
    expect(pipe.transform({de: 'kleiner Saal', en: ''})).toBe('kleiner Saal');
  });

  it('returns an empty string for missing values', () => {
    expect(pipe.transform(null)).toBe('');
    expect(pipe.transform(undefined)).toBe('');
  });
});
