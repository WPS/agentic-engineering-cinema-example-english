import {inject, Pipe, PipeTransform} from '@angular/core';
import {LanguageService} from '../../common/i18n/language.service';

/** Formats a running time in minutes in the current UI language: "2 Std 15 Min" / "2 h 15 min". */
@Pipe({
  name: 'duration',
  pure: false,
})
export class DurationPipe implements PipeTransform {
  private readonly languageService = inject(LanguageService);

  transform(minutes: number): string {
    if (!minutes && minutes !== 0) return '';

    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;

    const parts: string[] = [];
    if (hours > 0) {
      parts.push(this.languageService.translate('film.duration.hours', {hours}));
    }
    if (mins > 0) {
      parts.push(this.languageService.translate('film.duration.minutes', {minutes: mins}));
    }
    return parts.join(' ');
  }
}
