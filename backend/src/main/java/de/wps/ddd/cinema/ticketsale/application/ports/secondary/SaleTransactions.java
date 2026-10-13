package de.wps.ddd.cinema.ticketsale.application.ports.secondary;

import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;
import org.jmolecules.ddd.annotation.Repository;

@Repository
public interface SaleTransactions {

    /** Saves (inserts or updates) the sale transaction including its current payment attempt. */
    void save(SaleTransaction saleTransaction);

    /** Returns the sale transaction of the order; throws {@code ResourceNotFound} if it does not exist. */
    SaleTransaction get(OrderNumber orderNumber);

    /** Returns the sale transaction whose current payment attempt has the given id; throws {@code ResourceNotFound} otherwise. */
    SaleTransaction getByPaymentAttempt(PaymentAttemptId paymentAttemptId);
}
