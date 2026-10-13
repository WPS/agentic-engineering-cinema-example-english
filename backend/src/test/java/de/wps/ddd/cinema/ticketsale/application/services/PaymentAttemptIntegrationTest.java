package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.IntegrationTestWithWeeklyPlan;
import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransactionStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttempt;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentCancelled;
import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentReceived;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.GetPaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.StartSaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.StartPaymentAttempt;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.ProcessPaymentCancellation;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.ProcessPaymentReceipt;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SaleTransactions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PaymentAttemptIntegrationTest extends IntegrationTestWithWeeklyPlan {

    @Autowired
    private StartSaleTransaction startSaleTransaction;

    @Autowired
    private StartPaymentAttempt startPaymentAttempt;

    @Autowired
    private GetPaymentStatus getPaymentStatus;

    @Autowired
    private ProcessPaymentReceipt processPaymentReceipt;

    @Autowired
    private ProcessPaymentCancellation processPaymentCancellation;

    @Autowired
    private SaleTransactions saleTransactions;

    private final AdjacentSeats seats =
            new AdjacentSeats(List.of(new SeatId(new RowNumber(4), new SeatNumber(1))));

    private ScreeningId screeningId() {
        return screeningId("The Fast and the Curious", SMALL_HALL, LocalDateTime.of(2025, 3, 23, 14, 30));
    }

    private OrderNumber transactionInProgress() {
        return startSaleTransaction.execute(screeningId(), seats).getOrderNumber();
    }

    @Test
    void startSaleTransaction_calculatesThePriceItselfAndCreatesNoPaymentAttemptYet() {
        // act
        var saleTransaction = startSaleTransaction.execute(screeningId(), seats);

        // assert
        assertThat(saleTransaction.getTotalPrice().getAmount()).isPositive();
        assertThat(saleTransaction.getStatus()).isEqualTo(SaleTransactionStatus.IN_PROGRESS);
        assertThat(saleTransaction.paymentAttempt()).isEmpty();

        assertThatThrownBy(() -> getPaymentStatus.execute(saleTransaction.getOrderNumber()))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("has no payment attempt yet");
    }

    @Test
    void startPaymentAttempt_firstAttempt_coversTheAgreedPrice() {
        // arrange
        var orderNumber = transactionInProgress();

        // act
        var paymentAttempt = startPaymentAttempt.execute(orderNumber);

        // assert
        assertThat(paymentAttempt.getAttempt()).isEqualTo(1);
        assertThat(paymentAttempt.getAmount()).isEqualTo(saleTransactions.get(orderNumber).getTotalPrice());
        assertThat(getPaymentStatus.execute(orderNumber)).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void startPaymentAttempt_afterCancellation_replacesItAtTheSamePrice() {
        // arrange
        var orderNumber = transactionInProgress();
        var agreedPrice = saleTransactions.get(orderNumber).getTotalPrice();
        var firstAttempt = startPaymentAttempt.execute(orderNumber);
        processPaymentCancellation.execute(new PaymentCancelled(firstAttempt.getId()));

        // act
        var secondAttempt = startPaymentAttempt.execute(orderNumber);

        // assert
        assertThat(secondAttempt.getAttempt()).isEqualTo(2);
        assertThat(secondAttempt.getId()).isNotEqualTo(firstAttempt.getId());
        assertThat(secondAttempt.getAmount()).isEqualTo(agreedPrice);
        assertThat(getPaymentStatus.execute(orderNumber)).isEqualTo(PaymentStatus.PENDING);

        var saved = saleTransactions.get(orderNumber);
        assertThat(saved.paymentAttempt()).get()
                .extracting(PaymentAttempt::getId).isEqualTo(secondAttempt.getId());
        assertThat(saved.getAttempts()).isEqualTo(2);

        // and the second PaymentAttempt completes the sale
        processPaymentReceipt.execute(new PaymentReceived(secondAttempt.getId()));
        assertThat(getPaymentStatus.execute(orderNumber)).isEqualTo(PaymentStatus.RECEIVED);
        assertThat(saleTransactions.get(orderNumber).getStatus())
                .isEqualTo(SaleTransactionStatus.COMPLETED);
    }

    @Test
    void processPaymentCancellation_keepsTheSaleTransactionInProgress() {
        // arrange
        var orderNumber = transactionInProgress();
        var paymentAttempt = startPaymentAttempt.execute(orderNumber);

        // act
        processPaymentCancellation.execute(new PaymentCancelled(paymentAttempt.getId()));

        // assert
        assertThat(getPaymentStatus.execute(orderNumber)).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(saleTransactions.get(orderNumber).getStatus())
                .isEqualTo(SaleTransactionStatus.IN_PROGRESS);
    }

    @Test
    void startPaymentAttempt_whileAnAttemptIsPending_throwsBusinessRuleViolated() {
        // arrange
        var orderNumber = transactionInProgress();
        startPaymentAttempt.execute(orderNumber);

        // act / assert
        assertThatThrownBy(() -> startPaymentAttempt.execute(orderNumber))
                .isInstanceOf(BusinessRuleViolated.class)
                .hasMessageContaining("A payment attempt is already pending");
    }

    @Test
    void startPaymentAttempt_afterCompletion_throwsBusinessRuleViolated() {
        // arrange
        var orderNumber = transactionInProgress();
        var paymentAttempt = startPaymentAttempt.execute(orderNumber);
        processPaymentReceipt.execute(new PaymentReceived(paymentAttempt.getId()));

        // act / assert
        assertThatThrownBy(() -> startPaymentAttempt.execute(orderNumber))
                .isInstanceOf(BusinessRuleViolated.class)
                .hasMessageContaining("completed sale transaction");
    }

    @Test
    void startPaymentAttempt_unknownOrderNumber_throwsResourceNotFound() {
        // arrange
        var unknownOrderNumber = new OrderNumber(UUID.randomUUID());

        // act / assert
        assertThatThrownBy(() -> startPaymentAttempt.execute(unknownOrderNumber))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void processPaymentReceipt_unknownPaymentAttempt_throwsResourceNotFound() {
        // arrange
        var unknownPaymentAttempt = PaymentAttemptId.generate();

        // act / assert
        assertThatThrownBy(() -> processPaymentReceipt.execute(new PaymentReceived(unknownPaymentAttempt)))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No sale transaction exists for payment attempt");
    }

    @Test
    void getPaymentStatus_unknownOrderNumber_throwsResourceNotFound() {
        // arrange
        var unknownOrderNumber = new OrderNumber(UUID.randomUUID());

        // act / assert
        assertThatThrownBy(() -> getPaymentStatus.execute(unknownOrderNumber))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("does not exist");
    }
}
