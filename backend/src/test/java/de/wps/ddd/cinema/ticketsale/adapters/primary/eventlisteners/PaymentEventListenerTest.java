package de.wps.ddd.cinema.ticketsale.adapters.primary.eventlisteners;

import de.wps.ddd.cinema.payment.PaymentCancelledDto;
import de.wps.ddd.cinema.payment.PaymentReceivedDto;
import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentCancelled;
import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentReceived;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.ProcessPaymentCancellation;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.ProcessPaymentReceipt;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentEventListenerTest {

    private final ProcessPaymentReceipt processPaymentReceipt = mock(ProcessPaymentReceipt.class);
    private final ProcessPaymentCancellation processPaymentCancellation = mock(ProcessPaymentCancellation.class);
    private final PaymentEventListener listener =
            new PaymentEventListener(processPaymentReceipt, processPaymentCancellation);

    private final UUID reference = UUID.randomUUID();

    @Test
    void on_paymentReceivedDto_passesDomainEventWithPaymentAttemptId() {
        // act
        listener.on(new PaymentReceivedDto(reference));

        // assert
        verify(processPaymentReceipt).execute(new PaymentReceived(new PaymentAttemptId(reference)));
    }

    @Test
    void on_paymentCancelledDto_passesDomainEventWithPaymentAttemptId() {
        // act
        listener.on(new PaymentCancelledDto(reference));

        // assert
        verify(processPaymentCancellation).execute(new PaymentCancelled(new PaymentAttemptId(reference)));
    }
}
