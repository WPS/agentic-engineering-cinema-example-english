export interface Payment {
  reference: string,
  amountInCents: number,
  status: PaymentStatus,
}

export type PaymentStatus = 'OPEN' | 'RECEIVED' | 'CANCELLED';
