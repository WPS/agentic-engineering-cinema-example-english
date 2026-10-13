import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CheckoutComponent} from './checkout.component';
import {TicketSaleService} from '../../services/ticket-sale.service';
import {Currency, MoneyAmount, PaymentAttempt, SalePaymentStatus} from '../../models/ticket-sale';
import {of, throwError} from 'rxjs';

describe('CheckoutComponent', () => {
  let component: CheckoutComponent;
  let fixture: ComponentFixture<CheckoutComponent>;

  const amount: MoneyAmount = {amount: 5000, currency: Currency.EUR};

  const ticketSaleService = {
    getPrice: vi.fn(() => of(amount)),
    startSaleTransaction: vi.fn(),
    startPaymentAttempt: vi.fn(),
    getPaymentStatus: vi.fn(),
  } as unknown as TicketSaleService;

  beforeEach(async () => {
    vi.useFakeTimers();
    vi.mocked(ticketSaleService.getPaymentStatus).mockReset();
    vi.mocked(ticketSaleService.startSaleTransaction).mockReset();
    vi.mocked(ticketSaleService.startPaymentAttempt).mockReset();

    await TestBed.configureTestingModule({
      providers: [
        {provide: TicketSaleService, useValue: ticketSaleService},
        CheckoutComponent
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CheckoutComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('screening', {
      id: '123e4567-e89b-12d3-a456-426614174000',
      start: '2025-03-16T20:00:00',
      hall: {de: 'großer Saal', en: 'Large Hall'},
      film: 'Inception',
    });
    fixture.componentRef.setInput('seats', {seats: [{row: 1, number: 2}]});
    component.saleTransaction.set({
      orderNumber: 'a1',
      screeningId: '123e4567-e89b-12d3-a456-426614174000',
      seats: {seats: [{row: 1, number: 2}]},
      totalPrice: amount,
    });
    fixture.detectChanges();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  const paymentAttempt = (id: string, attempt: number): PaymentAttempt =>
    ({id, attempt, amount, status: 'PENDING'});

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('stops processing and reports confirmation as soon as the status is received', async () => {
    // arrange
    vi.mocked(ticketSaleService.getPaymentStatus)
      .mockReturnValue(of<SalePaymentStatus>('RECEIVED'));
    let confirmed = false;
    component.paymentConfirmed.subscribe(() => (confirmed = true));

    // act
    component.paymentPaid();
    await vi.advanceTimersByTimeAsync(500);

    // assert
    expect(component.processing()).toBe(false);
    expect(component.done()).toBe(true);
    expect(confirmed).toBe(true);
  });

  it('stops processing and marks the payment cancelled when the status is cancelled', async () => {
    // arrange
    vi.mocked(ticketSaleService.getPaymentStatus)
      .mockReturnValue(of<SalePaymentStatus>('CANCELLED'));

    // act
    component.paymentPaid();
    await vi.advanceTimersByTimeAsync(500);

    // assert
    expect(component.processing()).toBe(false);
    expect(component.cancelled()).toBe(true);
    expect(component.done()).toBe(false);
  });

  it('opens the dialog with the id of the new payment attempt without creating a second transaction', () => {
    // arrange
    vi.mocked(ticketSaleService.startPaymentAttempt).mockReturnValue(of(paymentAttempt('v2', 2)));
    const dialog = {openDialog: vi.fn()};
    vi.spyOn(component, 'paymentDialog').mockReturnValue(dialog as never);
    component.cancelled.set(true);

    // act
    component.pay();

    // assert
    expect(ticketSaleService.startPaymentAttempt).toHaveBeenCalledWith('a1');
    expect(ticketSaleService.startSaleTransaction).not.toHaveBeenCalled();
    expect(dialog.openDialog).toHaveBeenCalledWith('v2');
    expect(component.paymentAttempt()?.attempt).toBe(2);
    expect(component.cancelled()).toBe(false);
  });

  it('does not start a new payment attempt while the running one is open', () => {
    // arrange
    const dialog = {openDialog: vi.fn()};
    vi.spyOn(component, 'paymentDialog').mockReturnValue(dialog as never);
    vi.mocked(ticketSaleService.startPaymentAttempt).mockReturnValue(of(paymentAttempt('v1', 1)));
    component.pay();

    // act
    component.pay();
    component.resumePayment();

    // assert
    expect(ticketSaleService.startPaymentAttempt).toHaveBeenCalledTimes(1);
    expect(component.paymentInProgress()).toBe(true);
    expect(dialog.openDialog).toHaveBeenNthCalledWith(2, 'v1');
  });

  it('reopens the dialog with the next payment attempt on retry', () => {
    // arrange
    const dialog = {openDialog: vi.fn()};
    vi.spyOn(component, 'paymentDialog').mockReturnValue(dialog as never);
    vi.mocked(ticketSaleService.startPaymentAttempt).mockReturnValueOnce(of(paymentAttempt('v1', 1)));
    component.pay();
    component.paymentCancelled();

    // act
    vi.mocked(ticketSaleService.startPaymentAttempt).mockReturnValueOnce(of(paymentAttempt('v2', 2)));
    component.pay();

    // assert
    expect(dialog.openDialog).toHaveBeenNthCalledWith(1, 'v1');
    expect(dialog.openDialog).toHaveBeenNthCalledWith(2, 'v2');
    expect(component.paymentAttempt()?.id).toBe('v2');
  });

  it('creates the sale transaction first on the first payment attempt', () => {
    // arrange
    component.saleTransaction.set(undefined);
    vi.mocked(ticketSaleService.startSaleTransaction).mockReturnValue(of({
      orderNumber: 'new1',
      screeningId: '123e4567-e89b-12d3-a456-426614174000',
      seats: {seats: [{row: 1, number: 2}]},
      totalPrice: amount,
    }));
    vi.mocked(ticketSaleService.startPaymentAttempt).mockReturnValue(of(paymentAttempt('v1', 1)));
    const dialog = {openDialog: vi.fn()};
    vi.spyOn(component, 'paymentDialog').mockReturnValue(dialog as never);

    // act
    component.pay();

    // assert
    expect(ticketSaleService.startSaleTransaction).toHaveBeenCalled();
    expect(ticketSaleService.startPaymentAttempt).toHaveBeenCalledWith('new1');
    expect(component.saleTransaction()?.orderNumber).toBe('new1');
    expect(dialog.openDialog).toHaveBeenCalledWith('v1');
  });

  it('stops processing on an error while polling', async () => {
    // arrange
    vi.mocked(ticketSaleService.getPaymentStatus)
      .mockReturnValue(throwError(() => new Error('network error')));

    // act
    component.paymentPaid();
    await vi.advanceTimersByTimeAsync(500);

    // assert
    expect(component.processing()).toBe(false);
    expect(component.cancelled()).toBe(true);
  });
});
