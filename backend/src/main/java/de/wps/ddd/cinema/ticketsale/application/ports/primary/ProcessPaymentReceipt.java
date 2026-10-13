package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.common.architecture.ApplicationService;
import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentReceived;

@ApplicationService
public interface ProcessPaymentReceipt {
    /** Completes the sale: issues the tickets, marks the seats as sold and publishes TicketsSold. */
    void execute(PaymentReceived paymentReceived);
}
