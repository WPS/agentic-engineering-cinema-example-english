import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Payment} from '../models/payment';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private http = inject(HttpClient);

  private paymentUrl: string = '/api/payments';

  public getPayment(reference: string): Observable<Payment> {
    return this.http.get<Payment>(`${this.paymentUrl}/${reference}`);
  }

  public pay(reference: string): Observable<void> {
    return this.http.post<void>(`${this.paymentUrl}/${reference}/pay`, {});
  }
}
