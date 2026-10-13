import {TestBed} from '@angular/core/testing';
import {MoneyAmountPipe} from './money-amount.pipe';
import {LanguageService} from '../i18n/language.service';

describe('MoneyAmountPipe', () => {

  let pipe: MoneyAmountPipe;
  let languageService: LanguageService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    languageService = TestBed.inject(LanguageService);
    pipe = TestBed.runInInjectionContext(() => new MoneyAmountPipe());
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  describe('transform (German)', () => {
    it('it should return correct string on correct input', () => {
      expect(pipe.transform(200)).toEqual("2,00");
    });

    it('it should return correct string on single-digit input', () => {
      expect(pipe.transform(2)).toEqual("0,02");
    });

    it('it should return correct string on two-digit input', () => {
      expect(pipe.transform(20)).toEqual("0,20");
    });

    it('it should return correct string on non-zero decimal input', () => {
      expect(pipe.transform(20.79845)).toEqual("0,20");
    });

    it('it should not add thousands separators', () => {
      expect(pipe.transform(123456)).toEqual("1234,56");
    });
  });

  describe('transform (English)', () => {
    beforeEach(() => languageService.setLanguage('en'));

    it('uses a decimal point', () => {
      expect(pipe.transform(1250)).toEqual("12.50");
    });

    it('pads small amounts', () => {
      expect(pipe.transform(2)).toEqual("0.02");
    });

    it('truncates fractional cents', () => {
      expect(pipe.transform(20.79845)).toEqual("0.20");
    });

    it('follows a switch back to German', () => {
      languageService.setLanguage('de');
      expect(pipe.transform(1250)).toEqual("12,50");
    });
  });
});
