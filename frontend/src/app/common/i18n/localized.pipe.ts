import {inject, Pipe, PipeTransform} from '@angular/core';
import {LanguageService} from './language.service';
import {LocalizedText} from './localized-text';

/**
 * `{{ screening.hall | localized }}` — picks the current UI language from bilingual backend content.
 * Falls back to German when the text for the current language is missing.
 */
@Pipe({
  name: 'localized',
  pure: false,
})
export class LocalizedPipe implements PipeTransform {
  private readonly languageService = inject(LanguageService);

  transform(value: LocalizedText | null | undefined): string {
    if (!value) {
      return '';
    }
    return value[this.languageService.language()] || value.de || '';
  }
}
