package de.wps.ddd.cinema.payment.domain;

import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    private final PaymentReference reference = new PaymentReference(UUID.randomUUID());

    @Test
    void newPayment_isOpen() {
        // act
        var payment = Payment.create(reference, new Amount(5000));

        // assert
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.OPEN);
        assertThat(payment.getReference()).isEqualTo(reference);
        assertThat(payment.getAmount()).isEqualTo(new Amount(5000));
    }

    @Test
    void markReceived_fromOpen_becomesReceived() {
        // arrange
        var payment = Payment.create(reference, new Amount(5000));

        // act
        payment.markReceived();

        // assert
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.RECEIVED);
    }

    @Test
    void markCancelled_fromOpen_becomesCancelled() {
        // arrange
        var payment = Payment.create(reference, new Amount(5000));

        // act
        payment.markCancelled();

        // assert
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    void markReceived_alreadyReceived_throwsBusinessRuleViolated() {
        // arrange
        var payment = Payment.create(reference, new Amount(5000)).markReceived();

        // act / assert
        assertThatThrownBy(payment::markReceived).isInstanceOf(BusinessRuleViolated.class);
    }

    @Test
    void markReceived_alreadyCancelled_throwsBusinessRuleViolated() {
        // arrange
        var payment = Payment.create(reference, new Amount(5000)).markCancelled();

        // act / assert
        assertThatThrownBy(payment::markReceived).isInstanceOf(BusinessRuleViolated.class);
    }

    @Test
    void markCancelled_alreadyReceived_throwsBusinessRuleViolated() {
        // arrange
        var payment = Payment.create(reference, new Amount(5000)).markReceived();

        // act / assert
        assertThatThrownBy(payment::markCancelled).isInstanceOf(BusinessRuleViolated.class);
    }

    @Test
    void markCancelled_alreadyCancelled_throwsBusinessRuleViolated() {
        // arrange
        var payment = Payment.create(reference, new Amount(5000)).markCancelled();

        // act / assert
        assertThatThrownBy(payment::markCancelled).isInstanceOf(BusinessRuleViolated.class);
    }
}
