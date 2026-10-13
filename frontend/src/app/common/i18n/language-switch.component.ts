import {Component, inject} from '@angular/core';
import {Language, LANGUAGES} from './language';
import {MessageKey} from './messages';
import {LanguageService} from './language.service';
import {TranslatePipe} from './translate.pipe';

@Component({
  selector: 'app-language-switch',
  imports: [TranslatePipe],
  template: `
    <div class="join" role="group" [attr.aria-label]="'common.language.label' | t">
      @for (language of languages; track language) {
        <button type="button"
                class="join-item btn btn-sm"
                [class.btn-active]="languageService.language() === language"
                [attr.aria-pressed]="languageService.language() === language"
                [attr.lang]="language"
                [title]="names[language] | t"
                (click)="languageService.setLanguage(language)">
          {{ language.toUpperCase() }}
        </button>
      }
    </div>
  `,
})
export class LanguageSwitchComponent {
  protected readonly languageService = inject(LanguageService);
  protected readonly languages = LANGUAGES;
  protected readonly names: Record<Language, MessageKey> = {
    de: 'common.language.de',
    en: 'common.language.en',
  };
}
