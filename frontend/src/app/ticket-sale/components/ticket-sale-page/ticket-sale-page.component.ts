import {Component, computed, input, signal} from '@angular/core';
import {ScreeningComponent} from '../screening/screening.component';
import {AdjacentSeats, SaleTransaction, Screening, Ticket} from '../../models/ticket-sale';
import {SeatCountComponent} from '../seat-count/seat-count.component';
import {SeatingPlanComponent} from '../seating-plan/seating-plan.component';
import {CheckoutComponent} from '../checkout/checkout.component';
import {TicketsComponent} from '../tickets/tickets.component';
import {NavbarComponent} from '../../../common/components/navbar/navbar.component';
import {StepBadgeComponent} from './step-badge/step-badge.component';
import {TranslatePipe} from '../../../common/i18n/translate.pipe';

@Component({
  selector: 'app-ticket-sale-page',
  imports: [
    ScreeningComponent,
    SeatCountComponent,
    SeatingPlanComponent,
    CheckoutComponent,
    TicketsComponent,
    NavbarComponent,
    StepBadgeComponent,
    TranslatePipe,
  ],
  templateUrl: './ticket-sale-page.component.html',
  styleUrl: './ticket-sale-page.component.css',
})
export class TicketSalePageComponent {
  readonly screeningId = input<string>();

  readonly selectedScreening = signal<Screening | undefined>(undefined);
  readonly selectedSeatCount = signal<number | undefined>(undefined);
  readonly selectedSeats = signal<AdjacentSeats | undefined>(undefined);
  readonly paymentConfirmation = signal<SaleTransaction | undefined>(undefined);
  readonly receivedTickets = signal<Ticket[] | undefined>(undefined);

  readonly showScreeningActive = computed(() => this.screeningId() !== undefined);
  readonly showScreeningDone = computed(() => this.selectedScreening() !== undefined);
  readonly showSeatCountActive = computed(
    () => this.selectedScreening() !== undefined && this.selectedSeatCount() === undefined,
  );
  readonly showSeatCountDone = computed(() => this.selectedSeatCount() !== undefined);
  readonly showSeatSelectionActive = computed(
    () => this.selectedSeatCount() !== undefined && this.selectedSeats() === undefined,
  );
  readonly showSeatSelectionDone = computed(() => this.selectedSeats() !== undefined);
  readonly showPaymentActive = computed(
    () => this.selectedSeats() !== undefined && this.paymentConfirmation() === undefined,
  );
  readonly showPaymentDone = computed(() => this.paymentConfirmation() !== undefined);
  readonly showTicketsActive = computed(
    () => this.paymentConfirmation() !== undefined && this.receivedTickets() === undefined,
  );
  readonly showTicketsDone = computed(() => this.receivedTickets() !== undefined);

  screeningLoaded(screening: Screening) {
    this.selectedScreening.set(screening);
  }

  seatCountSelected(seatCount: number) {
    this.selectedSeatCount.set(seatCount);
  }

  seatsSelected(seats: AdjacentSeats) {
    this.selectedSeats.set(seats);
  }

  paymentConfirmed(paymentConfirmation: SaleTransaction) {
    this.paymentConfirmation.set(paymentConfirmation);
  }

  ticketsPrinted(tickets: Ticket[]) {
    this.receivedTickets.set(tickets);
  }
}
