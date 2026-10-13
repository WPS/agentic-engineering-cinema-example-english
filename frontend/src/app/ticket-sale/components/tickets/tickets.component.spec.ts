import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TicketsComponent} from './tickets.component';
import {provideHttpClient} from '@angular/common/http';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {registerLocaleData} from '@angular/common';
import de from '@angular/common/locales/de';
import {LanguageService} from '../../../common/i18n/language.service';
import {Currency, SaleTransaction} from '../../models/ticket-sale';

describe('TicketsComponent', () => {
  let component: TicketsComponent;
  let fixture: ComponentFixture<TicketsComponent>;

  beforeAll(() => {
    registerLocaleData(de);
  });

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        TicketsComponent
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TicketsComponent);
    component = fixture.componentInstance;
    const paymentConfirmation: SaleTransaction = {
      orderNumber: 'A-2025-000123',
      screeningId: '123e4567-e89b-12d3-a456-426614174000',
      seats: {seats: [{row: 1, number: 2}]},
      totalPrice: {
        amount: 36.0,
        currency: Currency.EUR,
      },
    };
    fixture.componentRef.setInput('paymentConfirmation', paymentConfirmation);
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows the hall name of each ticket in the current language', () => {
    TestBed.inject(HttpTestingController)
      .expectOne('/api/ticket-sale/sale-transactions/A-2025-000123/tickets')
      .flush([{film: 'Inception', start: '2025-03-16T20:00:00', hall: {de: 'kleiner Saal', en: 'Small Hall'}, row: 1, seat: 2}]);
    fixture.detectChanges();
    const element = fixture.nativeElement as HTMLElement;
    expect(element.textContent).toContain('kleiner Saal');

    TestBed.inject(LanguageService).setLanguage('en');
    fixture.detectChanges();
    expect(element.textContent).toContain('Small Hall');
  });
});
