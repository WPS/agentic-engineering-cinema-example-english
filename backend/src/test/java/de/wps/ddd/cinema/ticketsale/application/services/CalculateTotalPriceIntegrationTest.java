package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.IntegrationTestWithWeeklyPlan;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.CalculateTotalPrice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CalculateTotalPriceIntegrationTest extends IntegrationTestWithWeeklyPlan {

    @Autowired
    private CalculateTotalPrice calculateTotalPrice;

    @Test
    void calculateTotalPrice_fourSeatsInRow4_returnsTotalPrice() {
        // arrange — row 4, seats 1-4 (standard 10.50 + box 2.00 per seat)
        var screeningId = screeningId("The Fast and the Curious", SMALL_HALL, LocalDateTime.of(2025, 3, 23, 14, 30));
        var selectedSeats = new AdjacentSeats(List.of(
                seatId(4, 1), seatId(4, 2), seatId(4, 3), seatId(4, 4)));

        // act
        var price = calculateTotalPrice.execute(screeningId, selectedSeats);

        // assert
        assertThat(price).isEqualTo(MoneyAmount.euro(50, 0));
    }

    private static SeatId seatId(int row, int number) {
        return new SeatId(new RowNumber(row), new SeatNumber(number));
    }
}
