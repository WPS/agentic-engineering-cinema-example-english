import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PaymentDialogComponent} from './payment-dialog.component';
import {PaymentService} from '../../services/payment.service';
import {Payment} from '../../models/payment';
import {of, throwError} from 'rxjs';
import {LanguageService} from '../../../common/i18n/language.service';

describe('PaymentDialogComponent', () => {
  let component: PaymentDialogComponent;
  let fixture: ComponentFixture<PaymentDialogComponent>;

  const paymentService = {
    getPayment: vi.fn(),
    pay: vi.fn(),
  } as unknown as PaymentService;

  function paymentWith(status: Payment['status']): Payment {
    return {reference: 'r1', amountInCents: 5000, status};
  }

  beforeEach(async () => {
    localStorage.clear();
    vi.useFakeTimers();
    vi.mocked(paymentService.getPayment).mockReset();
    vi.mocked(paymentService.pay).mockReset();

    await TestBed.configureTestingModule({
      imports: [PaymentDialogComponent],
      providers: [
        {provide: PaymentService, useValue: paymentService}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(PaymentDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    // jsdom does not implement showModal/close reliably — stub them for the test.
    const dialog = fixture.nativeElement.querySelector('dialog') as HTMLDialogElement;
    dialog.showModal = vi.fn();
    dialog.close = vi.fn();
    component.reference.set('r1');
  });

  afterEach(() => {
    vi.useRealTimers();
    localStorage.clear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('emits paid and stops processing once the status is received', async () => {
    // arrange
    vi.mocked(paymentService.pay).mockReturnValue(of(undefined));
    vi.mocked(paymentService.getPayment).mockReturnValue(of(paymentWith('RECEIVED')));
    let paid = false;
    component.paid.subscribe(() => (paid = true));

    // act
    component.confirm();
    await vi.advanceTimersByTimeAsync(1000);

    // assert
    expect(paid).toBe(true);
    expect(component.processing()).toBe(false);
    expect(component.error()).toBe(false);
  });

  it('emits cancelled when the status is cancelled', async () => {
    // arrange
    vi.mocked(paymentService.pay).mockReturnValue(of(undefined));
    vi.mocked(paymentService.getPayment).mockReturnValue(of(paymentWith('CANCELLED')));
    let cancelled = false;
    component.cancelled.subscribe(() => (cancelled = true));

    // act
    component.confirm();
    await vi.advanceTimersByTimeAsync(1000);

    // assert
    expect(cancelled).toBe(true);
    expect(component.processing()).toBe(false);
  });

  it('makes the dialog usable again after an error', async () => {
    // arrange
    vi.mocked(paymentService.pay).mockReturnValue(of(undefined));
    vi.mocked(paymentService.getPayment).mockReturnValue(throwError(() => new Error('Network error')));

    // act
    component.confirm();
    await vi.advanceTimersByTimeAsync(1000);

    // assert
    expect(component.processing()).toBe(false);
    expect(component.error()).toBe(true);
  });

  describe('language', () => {
    function text(): string {
      return (fixture.nativeElement as HTMLElement).textContent!.replace(/\s+/g, ' ');
    }

    it('shows the dialog in German by default', () => {
      component.payment.set(paymentWith('OPEN'));
      fixture.detectChanges();

      expect(text()).toContain('Zu zahlender Betrag');
      expect(text()).toContain('50,00 EUR');
      expect(text()).toContain('Kreditkarte');
      expect(text()).toContain('Bezahlkumpel');
      expect(text()).toContain('Zahlung bestätigen');
      const closeButton = fixture.nativeElement.querySelector('form[method="dialog"] button') as HTMLButtonElement;
      expect(closeButton.getAttribute('aria-label')).toBe('Zahlung schließen');
    });

    it('shows the dialog in English when the language is switched', () => {
      TestBed.inject(LanguageService).setLanguage('en');
      component.payment.set(paymentWith('OPEN'));
      fixture.detectChanges();

      expect(text()).toContain('Payment');
      expect(text()).toContain('Amount due');
      expect(text()).toContain('50.00 EUR');
      expect(text()).toContain('How would you like to pay?');
      expect(text()).toContain('Credit card');
      expect(text()).toContain('PayBuddy');
      expect(text()).toContain('Pear Pay');
      expect(text()).toContain('Confirm payment');
      const closeButton = fixture.nativeElement.querySelector('form[method="dialog"] button') as HTMLButtonElement;
      expect(closeButton.getAttribute('aria-label')).toBe('Close payment');
    });

    it('shows the error text in English', () => {
      TestBed.inject(LanguageService).setLanguage('en');
      component.error.set(true);
      fixture.detectChanges();

      expect(text()).toContain('The payment could not be processed.');
    });
  });
});
