import {TestBed} from '@angular/core/testing';

import {TicketSaleService} from './ticket-sale.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';
import {Currency} from '../models/ticket-sale';

describe('TicketSaleService', () => {
  let service: TicketSaleService;
  let httpMock: HttpTestingController;

  const screeningId = '090c173a-3636-4980-865a-1ec859eb4f90';
  const orderNumber = '11111111-1111-1111-1111-111111111111';
  const hall = {de: 'großer Saal', en: 'Large Hall'};

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        TicketSaleService
      ]
    });
    service = TestBed.inject(TicketSaleService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getScreening loads the screening with its bilingual hall name', () => {
    let screening: unknown;
    service.getScreening(screeningId).subscribe(s => (screening = s));

    const req = httpMock.expectOne(`/api/ticket-sale/screenings/${screeningId}`);
    expect(req.request.method).toBe('GET');
    req.flush({id: screeningId, start: '2025-03-16T20:00:00', hall, film: 'Inception'});

    expect(screening).toEqual({id: screeningId, start: '2025-03-16T20:00:00', hall, film: 'Inception'});
  });

  it('getSeatingPlan loads rows of seats', () => {
    let seatingPlan: unknown;
    service.getSeatingPlan(screeningId).subscribe(s => (seatingPlan = s));

    const req = httpMock.expectOne(`/api/ticket-sale/seating-plans/${screeningId}`);
    expect(req.request.method).toBe('GET');
    req.flush({seats: [[{row: 1, number: 1, isFree: true}, {row: 1, number: 2, isFree: false}]]});

    expect(seatingPlan).toEqual({seats: [[{row: 1, number: 1, isFree: true}, {row: 1, number: 2, isFree: false}]]});
  });

  it('findAdjacentSeats sends the seat count as query parameter', () => {
    let seats: unknown;
    service.findAdjacentSeats(screeningId, 2).subscribe(s => (seats = s));

    const req = httpMock.expectOne(r =>
      r.url === `/api/ticket-sale/seating-plans/${screeningId}/adjacent-seats`);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('seatCount')).toBe('2');
    expect(req.request.urlWithParams)
      .toBe(`/api/ticket-sale/seating-plans/${screeningId}/adjacent-seats?seatCount=2`);
    req.flush({seats: [{row: 3, number: 5}, {row: 3, number: 6}]});

    expect(seats).toEqual({seats: [{row: 3, number: 5}, {row: 3, number: 6}]});
  });

  it('getPrice posts the price request and returns the amount', () => {
    let price: unknown;
    service.getPrice(screeningId, {seats: [{row: 4, number: 1}]}).subscribe(p => (price = p));

    const req = httpMock.expectOne('/api/ticket-sale/price-requests');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({screeningId, seats: {seats: [{row: 4, number: 1}]}});
    req.flush({amount: 1250, currency: 'EUR'});

    expect(price).toEqual({amount: 1250, currency: Currency.EUR});
  });

  it('startSaleTransaction posts the ticket order and returns the sale transaction', () => {
    let transaction: unknown;
    service.startSaleTransaction(screeningId, {seats: [{row: 4, number: 1}]}).subscribe(t => (transaction = t));

    const req = httpMock.expectOne('/api/ticket-sale/sale-transactions');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({screeningId, seats: {seats: [{row: 4, number: 1}]}});
    req.flush({
      orderNumber,
      screeningId,
      seats: {seats: [{row: 4, number: 1}]},
      totalPrice: {amount: 1250, currency: 'EUR'},
    }, {status: 201, statusText: 'Created'});

    expect(transaction).toEqual({
      orderNumber,
      screeningId,
      seats: {seats: [{row: 4, number: 1}]},
      totalPrice: {amount: 1250, currency: Currency.EUR},
    });
  });

  it('startPaymentAttempt posts to the payment attempts of the transaction without a body', () => {
    let attempt: unknown;
    service.startPaymentAttempt(orderNumber).subscribe(a => (attempt = a));

    const req = httpMock.expectOne(`/api/ticket-sale/sale-transactions/${orderNumber}/payment-attempts`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush({id: 'v2', attempt: 2, amount: {amount: 5000, currency: 'EUR'}, status: 'PENDING'},
      {status: 201, statusText: 'Created'});

    expect(attempt).toEqual({id: 'v2', attempt: 2, amount: {amount: 5000, currency: Currency.EUR}, status: 'PENDING'});
  });

  it.each(['PENDING', 'RECEIVED', 'CANCELLED'])('getPaymentStatus returns the status %s', (wireStatus) => {
    let status: unknown;
    service.getPaymentStatus(orderNumber).subscribe(s => (status = s));

    const req = httpMock.expectOne(`/api/ticket-sale/sale-transactions/${orderNumber}/payment-status`);
    expect(req.request.method).toBe('GET');
    req.flush({status: wireStatus});

    expect(status).toBe(wireStatus);
  });

  it('getTickets loads the tickets with their bilingual hall names', () => {
    let tickets: unknown;
    service.getTickets(orderNumber).subscribe(t => (tickets = t));

    const req = httpMock.expectOne(`/api/ticket-sale/sale-transactions/${orderNumber}/tickets`);
    expect(req.request.method).toBe('GET');
    req.flush([{film: 'Inception', start: '2025-03-16T20:00:00', hall, row: 3, seat: 5}]);

    expect(tickets).toEqual([{film: 'Inception', start: '2025-03-16T20:00:00', hall, row: 3, seat: 5}]);
  });
});
