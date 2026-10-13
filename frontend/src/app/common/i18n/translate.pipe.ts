import {inject, Pipe, PipeTransform} from '@angular/core';
import {LanguageService, MessageParams} from './language.service';
import {MessageKey} from './messages';

/** `{{ 'program.title' | t }}` or `{{ 'seats.row' | t: {row: 3} }}`. Re-renders on language switch. */
@Pipe({
  name: 't',
  pure: false,
})
export class TranslatePipe implements PipeTransform {
  private readonly languageService = inject(LanguageService);

  transform(key: MessageKey, params?: MessageParams): string {
    return this.languageService.translate(key, params);
  }
}
