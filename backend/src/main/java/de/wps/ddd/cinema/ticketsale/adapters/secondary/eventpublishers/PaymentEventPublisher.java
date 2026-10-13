package de.wps.ddd.cinema.ticketsale.adapters.secondary.eventpublishers;

import de.wps.ddd.cinema.payment.StartPaymentDto;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher implements PaymentProvider {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void startPayment(PaymentAttemptId paymentAttemptId, MoneyAmount amount) {
        eventPublisher.publishEvent(new StartPaymentDto(paymentAttemptId.value(), amount.getAmount()));
    }
}
