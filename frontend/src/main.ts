import {bootstrapApplication} from '@angular/platform-browser';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {appConfig} from './app/app.config';
import {AppComponent} from './app/app.component';
import {registerLocaleData} from '@angular/common';
import de from '@angular/common/locales/de';
import {inject, LOCALE_ID} from '@angular/core';
import {LanguageService} from './app/common/i18n/language.service';

registerLocaleData(de);

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withFetch()),
    // Locale at startup only; the i18n pipes (common/i18n) pass the current locale on every render.
    {provide: LOCALE_ID, useFactory: () => inject(LanguageService).localeId()},
    ...appConfig.providers
  ]
})
  .catch((err) => console.error(err));
