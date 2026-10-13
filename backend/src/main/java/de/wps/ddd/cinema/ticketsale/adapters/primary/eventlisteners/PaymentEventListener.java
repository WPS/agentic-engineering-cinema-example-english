package de.wps.ddd.cinema.ticketsale.adapters.primary.eventlisteners;

import de.wps.ddd.cinema.payment.PaymentCancelledDto;
import de.wps.ddd.cinema.payment.PaymentReceivedDto;
import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentCancelled;
import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentReceived;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.ProcessPaymentCancellation;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.ProcessPaymentReceipt;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventListener {

    private final ProcessPaymentReceipt processPaymentReceipt;

    private final ProcessPaymentCancellation processPaymentCancellation;

    @ApplicationModuleListener
    public void on(PaymentReceivedDto dto) {
        processPaymentReceipt.execute(new PaymentReceived(new PaymentAttemptId(dto.reference())));
    }

    @ApplicationModuleListener
    public void on(PaymentCancelledDto dto) {
        processPaymentCancellation.execute(new PaymentCancelled(new PaymentAttemptId(dto.reference())));
    }
}
