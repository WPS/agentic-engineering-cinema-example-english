package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.common.architecture.ApplicationService;
import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentCancelled;

@ApplicationService
public interface ProcessPaymentCancellation {
    /** Marks the cancelled payment attempt as cancelled; the sale transaction stays in progress. */
    void execute(PaymentCancelled paymentCancelled);
}
