import {Component, DestroyRef, inject, OnInit, input, output, signal} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Screening} from '../../models/ticket-sale';
import {TranslatePipe} from '../../../common/i18n/translate.pipe';
import {LocalizedDatePipe} from '../../../common/i18n/localized-date.pipe';
import {LocalizedPipe} from '../../../common/i18n/localized.pipe';
import {Router} from '@angular/router';
import {TicketSaleService} from '../../services/ticket-sale.service';

@Component({
  selector: 'app-screening',
  imports: [
    TranslatePipe,
    LocalizedDatePipe,
    LocalizedPipe,
  ],
  templateUrl: './screening.component.html',
  styleUrl: './screening.component.css',
})
export class ScreeningComponent implements OnInit {
  private router = inject(Router);
  private ticketSaleService = inject(TicketSaleService);
  private destroyRef = inject(DestroyRef);

  readonly screeningId = input.required<string>();

  readonly screeningLoaded = output<Screening>();

  readonly screening = signal<Screening | undefined>(undefined);

  ngOnInit(): void {
    this.ticketSaleService.getScreening(this.screeningId())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((screening: Screening) => {
        this.screening.set(screening);
        this.screeningLoaded.emit(screening);
      });
  }

  backToProgram() {
    this.router.navigate(['/program']).then(_ => this.screening.set(undefined));
  }
}
