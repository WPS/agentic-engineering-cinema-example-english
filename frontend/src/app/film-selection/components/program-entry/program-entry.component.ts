import {Component, inject, input} from '@angular/core';
import {Film} from '../../models/program';
import {DurationPipe} from '../../pipes/duration.pipe';
import {TranslatePipe} from '../../../common/i18n/translate.pipe';
import {LocalizedDatePipe} from '../../../common/i18n/localized-date.pipe';
import {LocalizedPipe} from '../../../common/i18n/localized.pipe';
import {Router} from '@angular/router';

@Component({
  selector: 'app-program-entry',
  imports: [
    DurationPipe,
    TranslatePipe,
    LocalizedDatePipe,
    LocalizedPipe,
  ],
  templateUrl: './program-entry.component.html',
  styleUrl: './program-entry.component.css',
})
export class ProgramEntryComponent {
  private router = inject(Router);

  public readonly film = input.required<Film>();

  selectScreening(screeningId: string) {
    this.router.navigate(['/ticket-sale', screeningId]);
  }
}
