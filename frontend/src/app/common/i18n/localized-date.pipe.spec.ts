import {TestBed} from '@angular/core/testing';
import {registerLocaleData} from '@angular/common';
import de from '@angular/common/locales/de';
import {LocalizedDatePipe} from './localized-date.pipe';
import {LanguageService} from './language.service';

describe('LocalizedDatePipe', () => {
  // 2026-03-07 18:05 local time
  const date = new Date(2026, 2, 7, 18, 5);

  let pipe: LocalizedDatePipe;
  let languageService: LanguageService;

  beforeAll(() => {
    registerLocaleData(de);
  });

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    languageService = TestBed.inject(LanguageService);
    pipe = TestBed.runInInjectionContext(() => new LocalizedDatePipe());
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('formats time and date the German way by default', () => {
    expect(pipe.transform(date, 'time')).toBe('18:05 Uhr');
    expect(pipe.transform(date, 'date')).toBe('07.03.2026');
  });

  it('formats time and date the English way', () => {
    languageService.setLanguage('en');
    expect(pipe.transform(date, 'time')).toBe('6:05 PM');
    expect(pipe.transform(date, 'date')).toBe('03/07/2026');
  });

  it('returns an empty string for missing values', () => {
    expect(pipe.transform(null, 'date')).toBe('');
    expect(pipe.transform(undefined, 'date')).toBe('');
    expect(pipe.transform('', 'date')).toBe('');
  });
});
