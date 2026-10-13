package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import lombok.AccessLevel;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.Optional;

@AggregateRoot
@Getter
public class SaleTransaction {

    @Identity
    private final OrderNumber orderNumber;
    private final ScreeningId screeningId;
    private final AdjacentSeats selectedSeats;
    private final MoneyAmount totalPrice;

    @Getter(AccessLevel.NONE)
    private PaymentAttempt paymentAttempt;
    private int attempts;
    private SaleTransactionStatus status;

    public SaleTransaction(OrderNumber orderNumber, ScreeningId screeningId,
                           AdjacentSeats selectedSeats, MoneyAmount totalPrice,
                           PaymentAttempt paymentAttempt, int attempts, SaleTransactionStatus status) {
        this.orderNumber = orderNumber;
        this.screeningId = screeningId;
        this.selectedSeats = selectedSeats;
        this.totalPrice = totalPrice;
        this.paymentAttempt = paymentAttempt;
        this.attempts = attempts;
        this.status = status;
    }

    public static SaleTransaction start(ScreeningId screeningId,
                                        AdjacentSeats selectedSeats,
                                        MoneyAmount totalPrice) {
        return new SaleTransaction(OrderNumber.generate(), screeningId, selectedSeats,
                totalPrice, null, 0, SaleTransactionStatus.IN_PROGRESS);
    }

    public PaymentAttempt startPaymentAttempt() {
        BusinessRuleViolated.throwIf(!isInProgress(),
                "A completed sale transaction cannot be paid again.");
        BusinessRuleViolated.throwIf(paymentAttempt != null && paymentAttempt.isPending(),
                "A payment attempt is already pending.");

        this.attempts++;
        this.paymentAttempt = PaymentAttempt.start(attempts, totalPrice);
        return paymentAttempt;
    }

    public void paymentReceived(PaymentAttemptId paymentAttemptId) {
        paymentAttempt(paymentAttemptId).paymentReceived();
    }

    public void paymentCancelled(PaymentAttemptId paymentAttemptId) {
        paymentAttempt(paymentAttemptId).paymentCancelled();
    }

    public void complete() {
        BusinessRuleViolated.throwIf(!isPaid(), "Payment has not been received yet.");
        BusinessRuleViolated.throwIf(!isInProgress(),
                "Only sale transactions in progress can be completed.");
        this.status = SaleTransactionStatus.COMPLETED;
    }

    public boolean isInProgress() {
        return this.status == SaleTransactionStatus.IN_PROGRESS;
    }

    public boolean isPaid() {
        return paymentAttempt != null && paymentAttempt.isReceived();
    }

    public Optional<PaymentAttempt> paymentAttempt() {
        return Optional.ofNullable(paymentAttempt);
    }

    private PaymentAttempt paymentAttempt(PaymentAttemptId paymentAttemptId) {
        ResourceNotFound.throwIf(paymentAttempt == null || !paymentAttempt.getId().equals(paymentAttemptId),
                "Payment attempt " + paymentAttemptId + " is not the current payment attempt of order "
                        + orderNumber);
        return paymentAttempt;
    }
}
