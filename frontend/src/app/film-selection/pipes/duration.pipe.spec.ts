import {TestBed} from '@angular/core/testing';
import {DurationPipe} from './duration.pipe';
import {LanguageService} from '../../common/i18n/language.service';

describe('DurationPipe', () => {

  let pipe: DurationPipe;
  let languageService: LanguageService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    languageService = TestBed.inject(LanguageService);
    pipe = TestBed.runInInjectionContext(() => new DurationPipe());
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('formats hours and minutes in German by default', () => {
    expect(pipe.transform(135)).toBe('2 Std 15 Min');
    expect(pipe.transform(120)).toBe('2 Std');
    expect(pipe.transform(45)).toBe('45 Min');
  });

  it('formats hours and minutes in English', () => {
    languageService.setLanguage('en');

    expect(pipe.transform(135)).toBe('2 h 15 min');
    expect(pipe.transform(120)).toBe('2 h');
    expect(pipe.transform(45)).toBe('45 min');
  });

  it('follows a language switch without a new pipe instance', () => {
    expect(pipe.transform(95)).toBe('1 Std 35 Min');

    languageService.setLanguage('en');

    expect(pipe.transform(95)).toBe('1 h 35 min');
  });

  it('returns an empty string for zero or a missing value', () => {
    expect(pipe.transform(0)).toBe('');
    expect(pipe.transform(undefined as unknown as number)).toBe('');
  });
});
