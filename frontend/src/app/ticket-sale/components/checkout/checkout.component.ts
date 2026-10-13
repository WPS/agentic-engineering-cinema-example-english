import {Component, DestroyRef, inject, input, OnInit, output, signal, viewChild} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {interval, Observable, of, switchMap, takeWhile, tap} from 'rxjs';
import {
  AdjacentSeats,
  MoneyAmount,
  PaymentAttempt,
  SaleTransaction,
  Screening,
} from '../../models/ticket-sale';
import {MoneyAmountPipe} from '../../../common/pipes/money-amount.pipe';
import {PaymentDialogComponent} from '../../../payment/components/payment-dialog/payment-dialog.component';
import {TicketSaleService} from '../../services/ticket-sale.service';
import {TranslatePipe} from '../../../common/i18n/translate.pipe';

@Component({
  selector: 'app-checkout',
  imports: [
    MoneyAmountPipe,
    PaymentDialogComponent,
    TranslatePipe,
  ],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.css'
})
export class CheckoutComponent implements OnInit {
  private ticketSaleService = inject(TicketSaleService);
  private destroyRef = inject(DestroyRef);

  readonly screening = input.required<Screening>();

  readonly seats = input.required<AdjacentSeats>();

  readonly paymentConfirmed = output<SaleTransaction>();

  readonly totalAmount = signal<MoneyAmount | undefined>(undefined);

  readonly saleTransaction = signal<SaleTransaction | undefined>(undefined);

  readonly paymentAttempt = signal<PaymentAttempt | undefined>(undefined);

  readonly done = signal(false);

  readonly processing = signal(false);

  readonly cancelled = signal(false);

  // A new payment attempt is only allowed once the running one has been received or has failed —
  // the same rule the sale transaction enforces. If the visitor closes the dialog, it is reopened.
  readonly paymentInProgress = signal(false);

  readonly paymentDialog = viewChild.required<PaymentDialogComponent>('paymentDialog');

  ngOnInit(): void {
    this.ticketSaleService.getPrice(this.screening().id, this.seats())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((totalAmount: MoneyAmount) => this.totalAmount.set(totalAmount));
  }

  pay() {
    if (this.paymentInProgress()) {
      return;
    }
    this.cancelled.set(false);
    this.createSaleTransactionIfNeeded()
      .pipe(
        switchMap((saleTransaction) =>
          this.ticketSaleService.startPaymentAttempt(saleTransaction.orderNumber)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((paymentAttempt: PaymentAttempt) => {
        this.paymentAttempt.set(paymentAttempt);
        this.paymentInProgress.set(true);
        this.paymentDialog().openDialog(paymentAttempt.id);
      });
  }

  resumePayment() {
    const running = this.paymentAttempt();
    if (running) {
      this.paymentDialog().openDialog(running.id);
    }
  }

  private createSaleTransactionIfNeeded(): Observable<SaleTransaction> {
    const existing = this.saleTransaction();
    if (existing) {
      return of(existing);
    }
    return this.ticketSaleService.startSaleTransaction(this.screening().id, this.seats())
      .pipe(tap((saleTransaction) => this.saleTransaction.set(saleTransaction)));
  }

  // The generic dialog reports the successful payment. Since the ticket sale's order status catches up
  // asynchronously (via an event), we briefly poll for RECEIVED here before moving on.
  paymentPaid() {
    this.processing.set(true);
    interval(500)
      .pipe(
        switchMap(() => this.ticketSaleService.getPaymentStatus(this.saleTransaction()!.orderNumber)),
        takeWhile((paymentStatus) => paymentStatus === 'PENDING', true),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (paymentStatus) => {
          if (paymentStatus === 'RECEIVED') {
            this.processing.set(false);
            this.paymentInProgress.set(false);
            this.paymentConfirmed.emit(this.saleTransaction()!);
            this.done.set(true);
          } else if (paymentStatus === 'CANCELLED') {
            this.processing.set(false);
            this.paymentInProgress.set(false);
            this.cancelled.set(true);
          }
        },
        error: () => {
          this.processing.set(false);
          this.paymentInProgress.set(false);
          this.cancelled.set(true);
        },
      });
  }

  paymentCancelled() {
    this.paymentInProgress.set(false);
    this.cancelled.set(true);
  }
}
