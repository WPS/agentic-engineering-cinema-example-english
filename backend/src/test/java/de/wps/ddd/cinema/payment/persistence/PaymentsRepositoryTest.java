package de.wps.ddd.cinema.payment.persistence;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.payment.domain.Amount;
import de.wps.ddd.cinema.payment.domain.Payment;
import de.wps.ddd.cinema.payment.domain.PaymentReference;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentsRepositoryTest {

    private final PaymentsRepository payments = new PaymentsRepository();
    private final PaymentReference reference = new PaymentReference(UUID.randomUUID());

    @Test
    void saveAndGet_returnsSavedPayment() {
        // arrange
        var payment = Payment.create(reference, new Amount(5000));
        payments.save(payment);

        // act / assert
        assertThat(payments.get(reference)).isSameAs(payment);
    }

    @Test
    void get_unknownReference_throwsResourceNotFound() {
        // act / assert
        assertThatThrownBy(() -> payments.get(reference))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("does not exist");
    }
}
