package de.wps.ddd.cinema.payment.application;

import de.wps.ddd.cinema.common.architecture.ApplicationService;
import de.wps.ddd.cinema.payment.StartPaymentDto;
import de.wps.ddd.cinema.payment.PaymentCancelledDto;
import de.wps.ddd.cinema.payment.PaymentReceivedDto;
import de.wps.ddd.cinema.payment.domain.Amount;
import de.wps.ddd.cinema.payment.domain.Payment;
import de.wps.ddd.cinema.payment.domain.Payments;
import de.wps.ddd.cinema.payment.domain.PaymentReference;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulated payment provider. Registers an open payment and settles it on request
 * (within 3–6 s, successful in 2 out of 3 cases).
 */
@Service
@ApplicationService
@RequiredArgsConstructor
public class PaymentProcessing {

    private final Payments payments;

    private final ApplicationEventPublisher events;

    private final TransactionTemplate transactionTemplate;

    @ApplicationModuleListener
    public void handle(StartPaymentDto command) {
        payments.save(Payment.create(
                new PaymentReference(command.reference()),
                new Amount(command.amountInCents())));
    }

    @Async
    public void pay(PaymentReference reference) {
        // The simulated processing time deliberately lies outside the transaction: wait first,
        // then commit the status change + the result event transactionally. The transaction is
        // required for the downstream @ApplicationModuleListener (AFTER_COMMIT) to fire; it is
        // programmatic via TransactionTemplate because the @Async call does not go through a proxy.
        sleep(processingDurationMillis());
        boolean successful = paymentSucceeds();
        transactionTemplate.executeWithoutResult(status -> complete(reference, successful));
    }

    private void complete(PaymentReference reference, boolean successful) {
        var payment = payments.get(reference);
        if (successful) {
            payments.save(payment.markReceived());
            events.publishEvent(new PaymentReceivedDto(reference.value()));
        } else {
            payments.save(payment.markCancelled());
            events.publishEvent(new PaymentCancelledDto(reference.value()));
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    long processingDurationMillis() {
        return 3000 + ThreadLocalRandom.current().nextInt(3001);
    }

    boolean paymentSucceeds() {
        return ThreadLocalRandom.current().nextInt(3) != 0;
    }
}
