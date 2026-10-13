import {formatDate} from '@angular/common';
import {inject, Pipe, PipeTransform} from '@angular/core';
import {Language} from './language';
import {LanguageService} from './language.service';

/** Named date formats, so each language can use its own conventions. */
export type DateFormat = 'weekday' | 'weekdayShort' | 'date' | 'dayMonth' | 'time' | 'dateTime';

const FORMATS: Record<Language, Record<DateFormat, string>> = {
  de: {
    weekday: 'EEEE',
    weekdayShort: 'EE',
    date: 'dd.MM.yyyy',
    dayMonth: 'dd.MM.',
    time: "HH:mm 'Uhr'",
    dateTime: "dd.MM.yyyy HH:mm 'Uhr'",
  },
  en: {
    weekday: 'EEEE',
    weekdayShort: 'EEE',
    date: 'MM/dd/yyyy',
    dayMonth: 'MM/dd',
    time: 'h:mm a',
    dateTime: 'MM/dd/yyyy h:mm a',
  },
};

/** `{{ screening.start | localizedDate: 'time' }}` — formats in the current UI language. */
@Pipe({
  name: 'localizedDate',
  pure: false,
})
export class LocalizedDatePipe implements PipeTransform {
  private readonly languageService = inject(LanguageService);

  transform(value: Date | string | number | null | undefined, format: DateFormat): string {
    if (value === null || value === undefined || value === '') {
      return '';
    }
    const language = this.languageService.language();
    return formatDate(value, FORMATS[language][format], this.languageService.localeId());
  }
}
