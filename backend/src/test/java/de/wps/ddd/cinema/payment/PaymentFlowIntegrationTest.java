package de.wps.ddd.cinema.payment;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.payment.application.PaymentProcessing;
import de.wps.ddd.cinema.payment.domain.Payments;
import de.wps.ddd.cinema.payment.domain.PaymentReference;
import de.wps.ddd.cinema.payment.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class PaymentFlowIntegrationTest {

    @Autowired
    private PaymentProcessing paymentProcessing;

    @Autowired
    private Payments payments;

    @Test
    void startAndPay_setsFinalStatusAsynchronously(Scenario scenario) {
        var reference = new PaymentReference(UUID.randomUUID());

        scenario.publish(new StartPaymentDto(reference.value(), 5000))
                .andWaitAtMost(Duration.ofSeconds(2))
                .andWaitForStateChange(() -> status(reference))
                .andVerify(status -> assertThat(status.get()).isEqualTo(PaymentStatus.OPEN));

        scenario.stimulate(() -> paymentProcessing.pay(reference))
                .andWaitAtMost(Duration.ofSeconds(10))
                .andWaitForStateChange(() -> status(reference), s -> s.get() != PaymentStatus.OPEN)
                .andVerify(status -> assertThat(status.get()).isIn(PaymentStatus.RECEIVED, PaymentStatus.CANCELLED));
    }

    private Optional<PaymentStatus> status(PaymentReference reference) {
        try {
            return Optional.of(payments.get(reference).getStatus());
        } catch (ResourceNotFound notRegisteredYet) {
            return Optional.empty();
        }
    }
}
