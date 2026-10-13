package de.wps.ddd.cinema.payment.domain;

import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {

    @Identity
    private final PaymentReference reference;
    private final Amount amount;
    private PaymentStatus status;

    public static Payment create(PaymentReference reference, Amount amount) {
        return new Payment(reference, amount, PaymentStatus.OPEN);
    }

    public Payment markReceived() {
        BusinessRuleViolated.throwIf(status != PaymentStatus.OPEN, "Only open payments can be received.");
        this.status = PaymentStatus.RECEIVED;
        return this;
    }

    public Payment markCancelled() {
        BusinessRuleViolated.throwIf(status != PaymentStatus.OPEN, "Only open payments can be cancelled.");
        this.status = PaymentStatus.CANCELLED;
        return this;
    }
}
