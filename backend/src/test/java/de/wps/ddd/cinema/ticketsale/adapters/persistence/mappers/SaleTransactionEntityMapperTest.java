package de.wps.ddd.cinema.ticketsale.adapters.persistence.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers.SaleTransactionEntityMapper;
import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.SaleTransactionEntity;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttempt;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransactionStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SaleTransactionEntityMapperTest {

    private final SaleTransactionEntityMapper mapper = new SaleTransactionEntityMapper();

    private final OrderNumber orderNumber =
            new OrderNumber(UUID.fromString("a095c8f6-6fa2-4f2e-acf1-52cee0698e74"));
    private final ScreeningId screeningId =
            new ScreeningId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90"));
    private final PaymentAttemptId paymentAttemptId =
            new PaymentAttemptId(UUID.fromString("0d1b5e1a-2c3d-4e5f-8a9b-0c1d2e3f4a5b"));

    @Test
    void toEntity() {
        // arrange
        var saleTransaction = new SaleTransaction(orderNumber, screeningId,
                new AdjacentSeats(List.of(seatId(4, 1), seatId(4, 2))),
                MoneyAmount.euro(25, 0),
                new PaymentAttempt(paymentAttemptId, 2, MoneyAmount.euro(25, 0), PaymentStatus.RECEIVED),
                2, SaleTransactionStatus.COMPLETED);

        // act
        var entity = mapper.toEntity(saleTransaction);

        // assert
        assertThat(entity.getOrderNumber()).isEqualTo(orderNumber.value());
        assertThat(entity.getScreeningId()).isEqualTo(screeningId.value());
        assertThat(entity.getTotalPriceInCents()).isEqualTo(2500);
        assertThat(entity.getStatus()).isEqualTo("COMPLETED");
        assertThat(entity.getSeats()).extracting(SaleTransactionEntity.SeatEmbeddable::getSeatNumber)
                .containsExactly(1, 2);
        assertThat(entity.getSeats()).allSatisfy(seat -> assertThat(seat.getRowNumber()).isEqualTo(4));
        assertThat(entity.getAttempts()).isEqualTo(2);
        assertThat(entity.getPaymentAttempt().getPaymentAttemptId()).isEqualTo(paymentAttemptId.value());
        assertThat(entity.getPaymentAttempt().getAttempt()).isEqualTo(2);
        assertThat(entity.getPaymentAttempt().getAmountInCents()).isEqualTo(2500);
        assertThat(entity.getPaymentAttempt().getStatus()).isEqualTo("RECEIVED");
    }

    @Test
    void toDomain() {
        // arrange
        var entity = new SaleTransactionEntity(orderNumber.value(), screeningId.value(), List.of(
                new SaleTransactionEntity.SeatEmbeddable(4, 1),
                new SaleTransactionEntity.SeatEmbeddable(4, 2)), 2500,
                new SaleTransactionEntity.PaymentAttemptEmbeddable(paymentAttemptId.value(), 1, 2500, "PENDING"),
                1, "IN_PROGRESS");

        // act
        var saleTransaction = mapper.toDomain(entity);

        // assert
        assertThat(saleTransaction.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(saleTransaction.getScreeningId()).isEqualTo(screeningId);
        assertThat(saleTransaction.getTotalPrice()).isEqualTo(MoneyAmount.euro(25, 0));
        assertThat(saleTransaction.getStatus()).isEqualTo(SaleTransactionStatus.IN_PROGRESS);
        assertThat(saleTransaction.getSelectedSeats().seats())
                .containsExactly(seatId(4, 1), seatId(4, 2));
        assertThat(saleTransaction.getAttempts()).isEqualTo(1);
        assertThat(saleTransaction.paymentAttempt()).get().satisfies(paymentAttempt -> {
            assertThat(paymentAttempt.getId()).isEqualTo(paymentAttemptId);
            assertThat(paymentAttempt.getAttempt()).isEqualTo(1);
            assertThat(paymentAttempt.getStatus()).isEqualTo(PaymentStatus.PENDING);
        });
    }

    private static SeatId seatId(int row, int number) {
        return new SeatId(new RowNumber(row), new SeatNumber(number));
    }
}
