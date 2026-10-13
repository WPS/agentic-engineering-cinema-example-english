package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.common.architecture.ApplicationService;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttempt;

@ApplicationService
public interface StartPaymentAttempt {
    /** Starts a new payment attempt over the order's total price and hands it to the payment provider. */
    PaymentAttempt execute(OrderNumber orderNumber);
}
