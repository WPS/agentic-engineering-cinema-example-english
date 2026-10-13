package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SaleTransactionTest {

    @Test
    void start_fixesOrderAndPriceAndBeginsWithoutPaymentAttempt() {
        // act
        var saleTransaction = start();

        // assert
        assertThat(saleTransaction.getOrderNumber()).isNotNull();
        assertThat(saleTransaction.getTotalPrice()).isEqualTo(MoneyAmount.euro(25, 0));
        assertThat(saleTransaction.getSelectedSeats().seats()).hasSize(2);
        assertThat(saleTransaction.getStatus()).isEqualTo(SaleTransactionStatus.IN_PROGRESS);
        assertThat(saleTransaction.paymentAttempt()).isEmpty();
        assertThat(saleTransaction.getAttempts()).isZero();
        assertThat(saleTransaction.isPaid()).isFalse();
    }

    @Test
    void startPaymentAttempt_firstAttempt_coversTheTotalPrice() {
        // arrange
        var saleTransaction = start();

        // act
        var paymentAttempt = saleTransaction.startPaymentAttempt();

        // assert
        assertThat(paymentAttempt.getAttempt()).isEqualTo(1);
        assertThat(paymentAttempt.getAmount()).isEqualTo(saleTransaction.getTotalPrice());
        assertThat(saleTransaction.paymentAttempt()).contains(paymentAttempt);
        assertThat(saleTransaction.getAttempts()).isEqualTo(1);
    }

    @Test
    void startPaymentAttempt_afterCancellation_replacesTheFailedAttempt() {
        // arrange
        var saleTransaction = start();
        var firstAttempt = saleTransaction.startPaymentAttempt();
        saleTransaction.paymentCancelled(firstAttempt.getId());

        // act
        var secondAttempt = saleTransaction.startPaymentAttempt();

        // assert
        assertThat(secondAttempt.getAttempt()).isEqualTo(2);
        assertThat(secondAttempt.getId()).isNotEqualTo(firstAttempt.getId());
        assertThat(secondAttempt.getAmount()).isEqualTo(firstAttempt.getAmount());
        assertThat(firstAttempt.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(saleTransaction.paymentAttempt()).contains(secondAttempt);
        assertThat(saleTransaction.getAttempts()).isEqualTo(2);
    }

    @Test
    void startPaymentAttempt_whileOneIsPending_throwsBusinessRuleViolated() {
        // arrange
        var saleTransaction = start();
        saleTransaction.startPaymentAttempt();

        // act / assert
        assertThatThrownBy(saleTransaction::startPaymentAttempt)
                .isInstanceOf(BusinessRuleViolated.class)
                .hasMessageContaining("A payment attempt is already pending");
    }

    @Test
    void startPaymentAttempt_afterCompletion_throwsBusinessRuleViolated() {
        // arrange
        var saleTransaction = paidTransaction();
        saleTransaction.complete();

        // act / assert
        assertThatThrownBy(saleTransaction::startPaymentAttempt)
                .isInstanceOf(BusinessRuleViolated.class)
                .hasMessageContaining("completed sale transaction");
    }

    @Test
    void paymentReceived_foreignPaymentAttempt_throwsResourceNotFound() {
        // arrange
        var saleTransaction = start();
        saleTransaction.startPaymentAttempt();

        // act / assert
        assertThatThrownBy(() -> saleTransaction.paymentReceived(PaymentAttemptId.generate()))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("is not the current payment attempt");
    }

    @Test
    void complete_afterPaymentReceived_endsTheTransaction() {
        // arrange
        var saleTransaction = paidTransaction();

        // act
        saleTransaction.complete();

        // assert
        assertThat(saleTransaction.getStatus()).isEqualTo(SaleTransactionStatus.COMPLETED);
        assertThat(saleTransaction.isInProgress()).isFalse();
    }

    @Test
    void complete_withoutPaymentReceived_throwsBusinessRuleViolated() {
        // arrange
        var saleTransaction = start();
        saleTransaction.startPaymentAttempt();

        // act / assert
        assertThatThrownBy(saleTransaction::complete)
                .isInstanceOf(BusinessRuleViolated.class)
                .hasMessageContaining("Payment has not been received yet");
    }

    @Test
    void complete_twice_throwsBusinessRuleViolated() {
        // arrange
        var saleTransaction = paidTransaction();
        saleTransaction.complete();

        // act / assert
        assertThatThrownBy(saleTransaction::complete)
                .isInstanceOf(BusinessRuleViolated.class)
                .hasMessageContaining("Only sale transactions in progress");
    }

    @Test
    void paymentCancelled_forAReplacedAttempt_throwsResourceNotFound() {
        // arrange
        var saleTransaction = start();
        var firstAttempt = saleTransaction.startPaymentAttempt();
        saleTransaction.paymentCancelled(firstAttempt.getId());
        saleTransaction.startPaymentAttempt();

        // act / assert
        assertThatThrownBy(() -> saleTransaction.paymentCancelled(firstAttempt.getId()))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("is not the current payment attempt");
    }

    private static SaleTransaction start() {
        return SaleTransaction.start(
                new ScreeningId(UUID.randomUUID()),
                new AdjacentSeats(List.of(
                        new SeatId(new RowNumber(4), new SeatNumber(1)),
                        new SeatId(new RowNumber(4), new SeatNumber(2)))),
                MoneyAmount.euro(25, 0));
    }

    private static SaleTransaction paidTransaction() {
        var saleTransaction = start();
        var paymentAttempt = saleTransaction.startPaymentAttempt();
        saleTransaction.paymentReceived(paymentAttempt.getId());
        return saleTransaction;
    }
}
