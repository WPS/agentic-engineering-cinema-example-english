package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.common.architecture.ApplicationService;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentStatus;

@ApplicationService
public interface GetPaymentStatus {
    /** Returns the status of the order's current payment attempt; fails if none has been started yet. */
    PaymentStatus execute(OrderNumber orderNumber);
}
