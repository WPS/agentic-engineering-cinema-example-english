import {Component, DestroyRef, inject, signal} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {ProgramService} from '../../services/program.service';
import {Film} from '../../models/program';
import {CalendarComponent} from '../calendar/calendar.component';
import {ProgramEntryComponent} from '../program-entry/program-entry.component';

import {format} from 'date-fns';
import {NavbarComponent} from '../../../common/components/navbar/navbar.component';
import {TranslatePipe} from '../../../common/i18n/translate.pipe';

@Component({
  selector: 'app-program-page',
  imports: [
    CalendarComponent,
    ProgramEntryComponent,
    NavbarComponent,
    TranslatePipe,
  ],
  templateUrl: './program-page.component.html',
  styleUrl: './program-page.component.css',
})
export class ProgramPageComponent {
  private programService = inject(ProgramService);
  private destroyRef = inject(DestroyRef);

  readonly films = signal<Film[] | undefined>(undefined);

  selectDate(date: Date) {
    this.programService.getProgram(format(date, "yyyy-MM-dd"))
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(films => this.films.set(films));
  }
}
