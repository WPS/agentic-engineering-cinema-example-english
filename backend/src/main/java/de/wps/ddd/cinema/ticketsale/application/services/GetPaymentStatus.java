package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SaleTransactions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetPaymentStatus implements de.wps.ddd.cinema.ticketsale.application.ports.primary.GetPaymentStatus {

    private final SaleTransactions saleTransactions;

    @Override
    public PaymentStatus execute(OrderNumber orderNumber) {
        var paymentAttempt = saleTransactions.get(orderNumber).paymentAttempt();
        ResourceNotFound.throwIf(paymentAttempt.isEmpty(),
                "Order " + orderNumber + " has no payment attempt yet");
        return paymentAttempt.get().getStatus();
    }
}
