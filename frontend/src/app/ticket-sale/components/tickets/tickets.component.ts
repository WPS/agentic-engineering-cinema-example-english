import {Component, DestroyRef, inject, OnInit, output, input, signal} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {SaleTransaction, Ticket} from '../../models/ticket-sale';
import {TicketSaleService} from '../../services/ticket-sale.service';
import {TranslatePipe} from '../../../common/i18n/translate.pipe';
import {LocalizedDatePipe} from '../../../common/i18n/localized-date.pipe';
import {LocalizedPipe} from '../../../common/i18n/localized.pipe';

@Component({
  selector: 'app-tickets',
  imports: [
    TranslatePipe,
    LocalizedDatePipe,
    LocalizedPipe,
],
  templateUrl: './tickets.component.html',
  styleUrl: './tickets.component.css'
})
export class TicketsComponent implements OnInit {
  private ticketSaleService = inject(TicketSaleService);
  private destroyRef = inject(DestroyRef);

  readonly paymentConfirmation = input.required<SaleTransaction>();

  readonly ticketsPrinted = output<Ticket[]>();

  readonly tickets = signal<Ticket[] | undefined>(undefined);

  readonly stacked = signal(true);

  ngOnInit(): void {
    const confirmation = this.paymentConfirmation();
    this.ticketSaleService.getTickets(confirmation.orderNumber)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((data: Ticket[]) => this.tickets.set(data));
  }

  printTickets() {
    this.ticketsPrinted.emit(this.tickets()!);
  }

  toggleStacked() {
    this.stacked.update(value => !value);
  }
}
