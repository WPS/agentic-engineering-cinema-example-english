package de.wps.ddd.cinema.ticketsale.application.ports.secondary;

import de.wps.ddd.cinema.common.architecture.Gateway;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;

@Gateway
public interface PaymentProvider {

    /** Asks the payment provider to collect the amount, referenced by the payment attempt's id. */
    void startPayment(PaymentAttemptId paymentAttemptId, MoneyAmount amount);
}
