package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttempt;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SaleTransactions;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class StartPaymentAttempt implements de.wps.ddd.cinema.ticketsale.application.ports.primary.StartPaymentAttempt {

    private final SaleTransactions saleTransactions;

    private final PaymentProvider paymentProvider;

    @Override
    public PaymentAttempt execute(OrderNumber orderNumber) {
        var saleTransaction = saleTransactions.get(orderNumber);
        var paymentAttempt = saleTransaction.startPaymentAttempt();
        saleTransactions.save(saleTransaction);
        paymentProvider.startPayment(paymentAttempt.getId(), paymentAttempt.getAmount());
        return paymentAttempt;
    }
}
