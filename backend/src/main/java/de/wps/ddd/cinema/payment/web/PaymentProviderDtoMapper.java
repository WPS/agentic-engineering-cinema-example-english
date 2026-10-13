package de.wps.ddd.cinema.payment.web;

import de.wps.ddd.cinema.payment.domain.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentProviderDtoMapper {

    public PaymentDto toDto(Payment payment) {
        return new PaymentDto(
                payment.getReference().value().toString(),
                payment.getAmount().cents(),
                payment.getStatus().name());
    }
}
