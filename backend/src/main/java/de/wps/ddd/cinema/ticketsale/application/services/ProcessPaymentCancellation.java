package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentCancelled;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SaleTransactions;
import lombok.RequiredArgsConstructor;
import org.jmolecules.event.annotation.DomainEventHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class ProcessPaymentCancellation implements de.wps.ddd.cinema.ticketsale.application.ports.primary.ProcessPaymentCancellation {

    private final SaleTransactions saleTransactions;

    @Override
    @DomainEventHandler
    public void execute(PaymentCancelled paymentCancelled) {
        var saleTransaction = saleTransactions.getByPaymentAttempt(paymentCancelled.paymentAttemptId());
        saleTransaction.paymentCancelled(paymentCancelled.paymentAttemptId());
        saleTransactions.save(saleTransaction);
    }
}
