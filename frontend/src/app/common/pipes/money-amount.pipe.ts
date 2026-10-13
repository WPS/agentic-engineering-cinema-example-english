import {inject, Pipe, PipeTransform} from '@angular/core';
import {LanguageService} from '../i18n/language.service';

const DECIMAL_SEPARATOR = {de: ',', en: '.'} as const;

/** Formats an amount in cents with two decimals, using the decimal separator of the UI language. */
@Pipe({
  name: 'moneyAmount',
  pure: false,
})
export class MoneyAmountPipe implements PipeTransform {
  private readonly languageService = inject(LanguageService);

  transform(amountInCents: number): string {
    const cents = Math.trunc(amountInCents).toString().padStart(3, "0");
    const separator = DECIMAL_SEPARATOR[this.languageService.language()];
    return cents.substring(0, cents.length - 2) + separator + cents.substring(cents.length - 2);
  }
}
