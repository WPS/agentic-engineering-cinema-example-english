import {Component, DestroyRef, ElementRef, inject, output, signal, viewChild} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {interval, retry, switchMap, takeWhile} from 'rxjs';
import {MoneyAmountPipe} from '../../../common/pipes/money-amount.pipe';
import {TranslatePipe} from '../../../common/i18n/translate.pipe';
import {Payment} from '../../models/payment';
import {PaymentService} from '../../services/payment.service';

@Component({
  selector: 'app-payment-dialog',
  imports: [
    MoneyAmountPipe,
    TranslatePipe
  ],
  templateUrl: './payment-dialog.component.html',
  styleUrl: './payment-dialog.component.css'
})
export class PaymentDialogComponent {
  private paymentService = inject(PaymentService);
  private destroyRef = inject(DestroyRef);

  readonly paid = output<void>();
  readonly cancelled = output<void>();

  readonly reference = signal<string | undefined>(undefined);
  readonly payment = signal<Payment | undefined>(undefined);
  readonly processing = signal(false);
  readonly error = signal(false);

  readonly dialogRef = viewChild.required<ElementRef<HTMLDialogElement>>('paymentDialogModal');

  openDialog(reference: string): void {
    this.reference.set(reference);
    this.payment.set(undefined);
    this.processing.set(false);
    this.error.set(false);
    this.dialogRef().nativeElement.showModal();
    // The payment is registered asynchronously during the payment attempt — briefly wait for it to become available.
    this.paymentService.getPayment(reference)
      .pipe(retry({count: 10, delay: 500}), takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (payment) => this.payment.set(payment),
        error: () => this.error.set(true),
      });
  }

  confirm(): void {
    const reference = this.reference();
    if (!reference) {
      return;
    }
    this.processing.set(true);
    this.error.set(false);
    this.paymentService.pay(reference)
      .pipe(
        switchMap(() => interval(1000).pipe(
          switchMap(() => this.paymentService.getPayment(reference)),
          takeWhile((payment) => payment.status === 'OPEN', true)
        )),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (payment) => {
          if (payment.status === 'RECEIVED') {
            this.close();
            this.paid.emit();
          } else if (payment.status === 'CANCELLED') {
            this.close();
            this.cancelled.emit();
          }
        },
        error: () => {
          this.processing.set(false);
          this.error.set(true);
        },
      });
  }

  private close(): void {
    this.processing.set(false);
    this.dialogRef().nativeElement.close();
  }
}
