package de.wps.ddd.cinema.ticketsale.adapters.persistence.repositories;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransactionStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SaleTransactions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SaleTransactionsIntegrationTest {

    @Autowired
    private SaleTransactions saleTransactions;

    @Test
    void saveAndGet_readsBackScreeningSeatsAndStatus() {
        // arrange
        var screeningId = new ScreeningId(UUID.randomUUID());
        var saleTransaction = SaleTransaction.start(screeningId,
                new AdjacentSeats(List.of(seatId(4, 1), seatId(4, 2))),
                MoneyAmount.euro(25, 0));

        // act
        saleTransactions.save(saleTransaction);
        var loaded = saleTransactions.get(saleTransaction.getOrderNumber());

        // assert
        assertThat(loaded.getOrderNumber()).isEqualTo(saleTransaction.getOrderNumber());
        assertThat(loaded.getScreeningId()).isEqualTo(screeningId);
        assertThat(loaded.getSelectedSeats().seats())
                .containsExactlyInAnyOrder(seatId(4, 1), seatId(4, 2));
        assertThat(loaded.getTotalPrice()).isEqualTo(MoneyAmount.euro(25, 0));
        assertThat(loaded.getStatus()).isEqualTo(SaleTransactionStatus.IN_PROGRESS);
    }

    @Test
    void save_persistsTheCurrentPaymentAttemptAndTheCompletion() {
        // arrange
        var saleTransaction = SaleTransaction.start(new ScreeningId(UUID.randomUUID()),
                new AdjacentSeats(List.of(seatId(4, 1))), MoneyAmount.euro(12, 50));
        var firstPaymentAttempt = saleTransaction.startPaymentAttempt();
        saleTransaction.paymentCancelled(firstPaymentAttempt.getId());
        var secondPaymentAttempt = saleTransaction.startPaymentAttempt();
        saleTransaction.paymentReceived(secondPaymentAttempt.getId());

        // act
        saleTransaction.complete();
        saleTransactions.save(saleTransaction);

        // assert
        var loaded = saleTransactions.get(saleTransaction.getOrderNumber());
        assertThat(loaded.getStatus()).isEqualTo(SaleTransactionStatus.COMPLETED);
        assertThat(loaded.isInProgress()).isFalse();
        assertThat(loaded.getAttempts()).isEqualTo(2);
        assertThat(loaded.paymentAttempt()).get().satisfies(paymentAttempt -> {
            assertThat(paymentAttempt.getId()).isEqualTo(secondPaymentAttempt.getId());
            assertThat(paymentAttempt.getAttempt()).isEqualTo(2);
            assertThat(paymentAttempt.getStatus()).isEqualTo(PaymentStatus.RECEIVED);
            assertThat(paymentAttempt.getAmount()).isEqualTo(MoneyAmount.euro(12, 50));
        });
    }

    @Test
    void getByPaymentAttempt_findsTheTransactionByThePaymentAttemptId() {
        // arrange
        var saleTransaction = SaleTransaction.start(new ScreeningId(UUID.randomUUID()),
                new AdjacentSeats(List.of(seatId(4, 1))), MoneyAmount.euro(12, 50));
        var paymentAttempt = saleTransaction.startPaymentAttempt();
        saleTransactions.save(saleTransaction);

        // act / assert
        assertThat(saleTransactions.getByPaymentAttempt(paymentAttempt.getId()).getOrderNumber())
                .isEqualTo(saleTransaction.getOrderNumber());
    }

    @Test
    void getByPaymentAttempt_unknownPaymentAttempt_throwsResourceNotFound() {
        assertThatThrownBy(() -> saleTransactions.getByPaymentAttempt(PaymentAttemptId.generate()))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("No sale transaction exists for payment attempt");
    }

    @Test
    void get_unknownOrderNumber_throwsResourceNotFound() {
        // arrange
        var unknownOrderNumber = new OrderNumber(UUID.randomUUID());

        // act / assert
        assertThatThrownBy(() -> saleTransactions.get(unknownOrderNumber))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("does not exist");
    }

    private static SeatId seatId(int row, int number) {
        return new SeatId(new RowNumber(row), new SeatNumber(number));
    }
}
