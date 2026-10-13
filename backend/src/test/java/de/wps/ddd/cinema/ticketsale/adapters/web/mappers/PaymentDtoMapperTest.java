package de.wps.ddd.cinema.ticketsale.adapters.web.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.PaymentDtoMapper;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.SeatingPlanDtoMapper;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
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

class PaymentDtoMapperTest {

    private final PaymentDtoMapper mapper = new PaymentDtoMapper(new SeatingPlanDtoMapper());

    @Test
    void toDto_saleTransaction_mapsOrderNumberSeatsAndTotalPrice() {
        // arrange
        var saleTransaction = new SaleTransaction(
                new OrderNumber(UUID.fromString("a095c8f6-6fa2-4f2e-acf1-52cee0698e74")),
                new ScreeningId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90")),
                new AdjacentSeats(List.of(new SeatId(new RowNumber(4), new SeatNumber(1)))),
                MoneyAmount.euro(12, 50),
                null,
                0,
                SaleTransactionStatus.IN_PROGRESS);

        // act
        var dto = mapper.toDto(saleTransaction);

        // assert
        assertThat(dto.orderNumber()).isEqualTo("a095c8f6-6fa2-4f2e-acf1-52cee0698e74");
        assertThat(dto.screeningId()).isEqualTo("090c173a-3636-4980-865a-1ec859eb4f90");
        assertThat(dto.totalPrice().amount()).isEqualTo(1250);
        assertThat(dto.seats().seats()).singleElement()
                .satisfies(seat -> assertThat(seat.row()).isEqualTo(4));
    }

    @Test
    void toDto_moneyAmount_mapsAmountAndCurrency() {
        // act
        var dto = mapper.toDto(MoneyAmount.euro(10, 50));

        // assert
        assertThat(dto.amount()).isEqualTo(1050);
        assertThat(dto.currency()).isEqualTo("EUR");
    }

    @Test
    void toDto_paymentStatus_mapsStatusAsString() {
        // act
        var dto = mapper.toDto(PaymentStatus.RECEIVED);

        // assert
        assertThat(dto.status()).isEqualTo("RECEIVED");
    }
}
