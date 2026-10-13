package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentAttemptTest {

    private static PaymentAttempt paymentAttempt() {
        return PaymentAttempt.start(1, MoneyAmount.euro(50, 0));
    }

    @Test
    void start_isPendingWithAttemptAmountAndOwnId() {
        // act
        var paymentAttempt = paymentAttempt();

        // assert
        assertThat(paymentAttempt.getId()).isNotNull();
        assertThat(paymentAttempt.getAttempt()).isEqualTo(1);
        assertThat(paymentAttempt.getAmount()).isEqualTo(MoneyAmount.euro(50, 0));
        assertThat(paymentAttempt.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(paymentAttempt.isPending()).isTrue();
        assertThat(paymentAttempt.isReceived()).isFalse();
    }

    @Test
    void start_givesEachPaymentAttemptItsOwnId() {
        assertThat(paymentAttempt().getId()).isNotEqualTo(paymentAttempt().getId());
    }

    @Test
    void paymentReceived_whenPending_becomesReceived() {
        // arrange
        var paymentAttempt = paymentAttempt();

        // act
        paymentAttempt.paymentReceived();

        // assert
        assertThat(paymentAttempt.getStatus()).isEqualTo(PaymentStatus.RECEIVED);
        assertThat(paymentAttempt.isReceived()).isTrue();
        assertThat(paymentAttempt.isPending()).isFalse();
    }

    @Test
    void paymentCancelled_whenPending_becomesCancelled() {
        // arrange
        var paymentAttempt = paymentAttempt();

        // act
        paymentAttempt.paymentCancelled();

        // assert
        assertThat(paymentAttempt.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(paymentAttempt.isPending()).isFalse();
    }

    @Test
    void paymentReceived_alreadyCancelled_throwsBusinessRuleViolated() {
        // arrange
        var paymentAttempt = paymentAttempt();
        paymentAttempt.paymentCancelled();

        // act / assert
        assertThatThrownBy(paymentAttempt::paymentReceived)
                .isInstanceOf(BusinessRuleViolated.class)
                .hasMessageContaining("Only pending payment attempts");
    }

    @Test
    void paymentCancelled_alreadyReceived_throwsBusinessRuleViolated() {
        // arrange
        var paymentAttempt = paymentAttempt();
        paymentAttempt.paymentReceived();

        // act / assert
        assertThatThrownBy(paymentAttempt::paymentCancelled)
                .isInstanceOf(BusinessRuleViolated.class);
    }

    @Test
    void paymentReceived_twice_throwsBusinessRuleViolated() {
        // arrange
        var paymentAttempt = paymentAttempt();
        paymentAttempt.paymentReceived();

        // act / assert
        assertThatThrownBy(paymentAttempt::paymentReceived)
                .isInstanceOf(BusinessRuleViolated.class);
    }
}
