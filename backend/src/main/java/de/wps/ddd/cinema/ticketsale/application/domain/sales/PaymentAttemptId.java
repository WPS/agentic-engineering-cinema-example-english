package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

/** Also the reference under which the PaymentProvider keeps the PaymentAttempt. */
@ValueObject
public record PaymentAttemptId(UUID value) {

    public static PaymentAttemptId generate() {
        return new PaymentAttemptId(UUID.randomUUID());
    }
}
