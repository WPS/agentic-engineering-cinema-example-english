import {LocalizedText} from '../../common/i18n/localized-text';

export interface Screening {
  id: string,
  start: string,
  hall: LocalizedText,
  film: string,
}

export interface SeatingPlan {
  seats: Seat[][];
}

export interface Seat {
  row: number,
  number: number,
  isFree: boolean,
}

export interface SeatId {
  row: number,
  number: number,
}

export interface AdjacentSeats {
  seats: SeatId[],
}

export interface MoneyAmount {
  amount: number,
  currency: Currency,
}

export enum Currency {
  EUR = 'EUR',
}

export interface SaleTransaction {
  orderNumber: string,
  screeningId: string,
  seats: AdjacentSeats,
  totalPrice: MoneyAmount,
}

export interface PaymentAttempt {
  id: string,
  attempt: number,
  amount: MoneyAmount,
  status: SalePaymentStatus,
}

export type SalePaymentStatus = 'PENDING' | 'RECEIVED' | 'CANCELLED';

export interface Ticket {
  film: string,
  start: string,
  hall: LocalizedText,
  row: number,
  seat: number,
}
