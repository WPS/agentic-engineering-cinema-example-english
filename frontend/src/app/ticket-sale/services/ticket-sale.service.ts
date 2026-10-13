import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {
  AdjacentSeats,
  MoneyAmount,
  PaymentAttempt,
  SaleTransaction,
  SalePaymentStatus,
  Screening,
  SeatingPlan,
  Ticket,
} from '../models/ticket-sale';

/** Request body of both the price request and the ticket order. */
interface TicketOrder {
  screeningId: string,
  seats: AdjacentSeats,
}

@Injectable({
  providedIn: 'root'
})
export class TicketSaleService {
  private http = inject(HttpClient);

  private ticketSaleUrl: string = '/api/ticket-sale';

  private transactionsUrl: string = `${this.ticketSaleUrl}/sale-transactions`;

  public getScreening(screeningId: string): Observable<Screening> {
    return this.http.get<Screening>(`${this.ticketSaleUrl}/screenings/${screeningId}`);
  }

  public getSeatingPlan(screeningId: string): Observable<SeatingPlan> {
    return this.http.get<SeatingPlan>(`${this.ticketSaleUrl}/seating-plans/${screeningId}`);
  }

  public findAdjacentSeats(screeningId: string, seatCount: number): Observable<AdjacentSeats> {
    return this.http.get<AdjacentSeats>(`${this.ticketSaleUrl}/seating-plans/${screeningId}/adjacent-seats`, {
      params: {seatCount},
    });
  }

  public getPrice(screeningId: string, seats: AdjacentSeats): Observable<MoneyAmount> {
    const order: TicketOrder = {screeningId, seats};
    return this.http.post<MoneyAmount>(`${this.ticketSaleUrl}/price-requests`, order);
  }

  public startSaleTransaction(screeningId: string, seats: AdjacentSeats): Observable<SaleTransaction> {
    const order: TicketOrder = {screeningId, seats};
    return this.http.post<SaleTransaction>(this.transactionsUrl, order);
  }

  public startPaymentAttempt(orderNumber: string): Observable<PaymentAttempt> {
    return this.http.post<PaymentAttempt>(`${this.transactionsUrl}/${orderNumber}/payment-attempts`, null);
  }

  public getPaymentStatus(orderNumber: string): Observable<SalePaymentStatus> {
    return this.http.get<{status: SalePaymentStatus}>(`${this.transactionsUrl}/${orderNumber}/payment-status`)
      .pipe(map(response => response.status));
  }

  public getTickets(orderNumber: string): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.transactionsUrl}/${orderNumber}/tickets`);
  }
}
