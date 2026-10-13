import {TestBed} from '@angular/core/testing';

import {PaymentService} from './payment.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';

describe('PaymentService', () => {
  let service: PaymentService;
  let httpMock: HttpTestingController;

  const reference = '11111111-1111-1111-1111-111111111111';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        PaymentService
      ]
    });
    service = TestBed.inject(PaymentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it.each(['OPEN', 'RECEIVED', 'CANCELLED'])('getPayment loads the payment via GET (status %s)', (status) => {
    let payment: unknown;
    service.getPayment(reference).subscribe(p => (payment = p));

    const req = httpMock.expectOne(`/api/payments/${reference}`);
    expect(req.request.method).toBe('GET');
    req.flush({reference, amountInCents: 5000, status});

    expect(payment).toEqual({reference, amountInCents: 5000, status});
  });

  it('pay triggers the payment via POST', () => {
    service.pay(reference).subscribe();

    const req = httpMock.expectOne(`/api/payments/${reference}/pay`);
    expect(req.request.method).toBe('POST');
    req.flush(null, {status: 202, statusText: 'Accepted'});
  });
});
