package de.wps.ddd.cinema.payment.persistence;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.payment.domain.Payment;
import de.wps.ddd.cinema.payment.domain.Payments;
import de.wps.ddd.cinema.payment.domain.PaymentReference;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class PaymentsRepository implements Payments {

    private final ConcurrentHashMap<PaymentReference, Payment> payments = new ConcurrentHashMap<>();

    @Override
    public void save(Payment payment) {
        payments.put(payment.getReference(), payment);
    }

    @Override
    public Payment get(PaymentReference reference) {
        var payment = payments.get(reference);
        ResourceNotFound.throwIf(payment == null, "Payment " + reference.value() + " does not exist");
        return payment;
    }
}
