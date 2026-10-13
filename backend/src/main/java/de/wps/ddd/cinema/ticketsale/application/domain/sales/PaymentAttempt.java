package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity
@Getter
@AllArgsConstructor
public class PaymentAttempt {

    @Identity
    private final PaymentAttemptId id;
    /** Ordinal number within the sale transaction, not an identity. */
    private final int attempt;
    private final MoneyAmount amount;
    private PaymentStatus status;

    static PaymentAttempt start(int attempt, MoneyAmount amount) {
        return new PaymentAttempt(PaymentAttemptId.generate(), attempt, amount, PaymentStatus.PENDING);
    }

    void paymentReceived() {
        BusinessRuleViolated.throwIf(!isPending(), "Only pending payment attempts can be received.");
        this.status = PaymentStatus.RECEIVED;
    }

    void paymentCancelled() {
        BusinessRuleViolated.throwIf(!isPending(), "Only pending payment attempts can be cancelled.");
        this.status = PaymentStatus.CANCELLED;
    }

    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }

    public boolean isReceived() {
        return this.status == PaymentStatus.RECEIVED;
    }
}
