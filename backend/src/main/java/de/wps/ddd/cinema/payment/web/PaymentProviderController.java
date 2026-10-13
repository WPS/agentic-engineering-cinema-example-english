package de.wps.ddd.cinema.payment.web;

import de.wps.ddd.cinema.payment.application.PaymentProcessing;
import de.wps.ddd.cinema.payment.domain.Payments;
import de.wps.ddd.cinema.payment.domain.PaymentReference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
class PaymentProviderController {

    private final Payments payments;
    private final PaymentProcessing paymentProcessing;
    private final PaymentProviderDtoMapper paymentMapper;

    @GetMapping("/{reference}")
    PaymentDto get(@PathVariable UUID reference) {
        return paymentMapper.toDto(payments.get(new PaymentReference(reference)));
    }

    @PostMapping("/{reference}/pay")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void pay(@PathVariable UUID reference) {
        var paymentReference = new PaymentReference(reference);
        payments.get(paymentReference);
        paymentProcessing.pay(paymentReference);
    }
}
