import {Component, DestroyRef, inject, OnInit, output, signal} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {TranslatePipe} from '../../../common/i18n/translate.pipe';
import {LocalizedDatePipe} from '../../../common/i18n/localized-date.pipe';
import {DateService} from '../../../common/services/date.service';
import {format, isBefore, startOfDay} from 'date-fns';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {distinctUntilChanged, map} from 'rxjs';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-calendar',
  imports: [
    TranslatePipe,
    LocalizedDatePipe,
    FormsModule,
  ],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.css'
})
export class CalendarComponent implements OnInit {
  private dateService = inject(DateService);
  private router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);

  readonly today: Date = startOfDay(new Date());

  readonly selectableDates = signal<Date[]>([this.today]);

  readonly selectedDate = signal<Date>(this.today);

  readonly dateSelected = output<Date>();

  ngOnInit(): void {
    this.selectableDates.set(this.dateService.getWeekdays(this.today));
    this.activatedRoute.queryParamMap.pipe(
      map((queryParamMap) => queryParamMap.get('date')),
      map((value) => value ? new Date(value) : this.today),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef))
      .subscribe(value => {
        const day = startOfDay(value ?? this.today);
        this.selectedDate.set(day);
        this.dateSelected.emit(day);
      });
  }

  selectDate(date: Date): void {
    this.navigate({date: format(date, 'yyyy-MM-dd')});
  }

  isBeforeToday(date: Date): boolean {
    return isBefore(startOfDay(date), this.today);
  }

  navigate(queryParams: Params): Promise<boolean> {
    return this.router.navigate([], {
      queryParams, queryParamsHandling: 'merge', replaceUrl: true,
    });
  }
}
