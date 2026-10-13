package de.wps.ddd.cinema.payment.application;

import de.wps.ddd.cinema.payment.StartPaymentDto;
import de.wps.ddd.cinema.payment.PaymentCancelledDto;
import de.wps.ddd.cinema.payment.PaymentReceivedDto;
import de.wps.ddd.cinema.payment.domain.Amount;
import de.wps.ddd.cinema.payment.domain.Payment;
import de.wps.ddd.cinema.payment.domain.PaymentReference;
import de.wps.ddd.cinema.payment.domain.PaymentStatus;
import de.wps.ddd.cinema.payment.persistence.PaymentsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentProcessingTest {

    private final PaymentsRepository payments = new PaymentsRepository();
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final TransactionTemplate transactionTemplate = transactionTemplateThatRunsDirectly();

    private final PaymentReference reference = new PaymentReference(UUID.randomUUID());

    @Test
    void handle_createsOpenPayment() {
        // act
        processingWithOutcome(true).handle(new StartPaymentDto(reference.value(), 5000));

        // assert
        var payment = payments.get(reference);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.OPEN);
        assertThat(payment.getAmount()).isEqualTo(new Amount(5000));
    }

    @Test
    void pay_success_setsReceivedAndPublishes() {
        // arrange
        payments.save(Payment.create(reference, new Amount(5000)));

        // act
        processingWithOutcome(true).pay(reference);

        // assert
        assertThat(payments.get(reference).getStatus()).isEqualTo(PaymentStatus.RECEIVED);
        verify(events).publishEvent(new PaymentReceivedDto(reference.value()));
    }

    @Test
    void pay_failure_setsCancelledAndPublishes() {
        // arrange
        payments.save(Payment.create(reference, new Amount(5000)));

        // act
        processingWithOutcome(false).pay(reference);

        // assert
        assertThat(payments.get(reference).getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        verify(events).publishEvent(new PaymentCancelledDto(reference.value()));
    }

    // Processing with a deterministic outcome and no waiting time (test seams overridden)
    private PaymentProcessing processingWithOutcome(boolean successful) {
        return new PaymentProcessing(payments, events, transactionTemplate) {
            @Override
            long processingDurationMillis() {
                return 0;
            }

            @Override
            boolean paymentSucceeds() {
                return successful;
            }
        };
    }

    // Replaces the Spring transaction (missing in a unit test): simply runs the callback directly.
    private static TransactionTemplate transactionTemplateThatRunsDirectly() {
        var template = mock(TransactionTemplate.class);
        doAnswer(invocation -> {
            invocation.<Consumer<TransactionStatus>>getArgument(0).accept(null);
            return null;
        }).when(template).executeWithoutResult(any());
        return template;
    }
}
