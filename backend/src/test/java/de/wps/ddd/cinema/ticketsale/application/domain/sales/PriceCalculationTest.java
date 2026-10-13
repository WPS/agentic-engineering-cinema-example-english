package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Seat;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCategory;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PriceCalculationTest {

    private final PriceCalculation priceCalculation = new PriceCalculation();
    private final ScreeningId screeningId = new ScreeningId(UUID.fromString("a095c8f6-6fa2-4f2e-acf1-52cee0698e74"));

    @ParameterizedTest
    @CsvSource({
            "STANDARD,   STALLS, 10, 50",
            "STANDARD,   BOX,    12, 50",
            "PRIME_TIME, STALLS, 12,  0",
            "PRIME_TIME, BOX,    14,  0",
            "CHILDREN,   STALLS,  8, 50",
            "CHILDREN,   BOX,    10, 50",
    })
    void calculatePrice_oneSeat_combinesScreeningAndSeatPrice(
            ScreeningCategory screeningCategory, SeatCategory seatCategory, int euro, int cents) {
        // arrange
        var screening = screening(screeningCategory);
        var seatId = new SeatId(new RowNumber(1), new SeatNumber(1));
        var seatingPlan = new SeatingPlan(screeningId, List.of(new Seat(seatId, seatCategory, false)));

        // act
        var price = priceCalculation.calculatePrice(screening, seatingPlan, new AdjacentSeats(List.of(seatId)));

        // assert
        assertThat(price).isEqualTo(MoneyAmount.euro(euro, cents));
    }

    @Test
    void calculatePrice_severalSeats_sumsSinglePrices() {
        // arrange — 2x stalls + 1x box at standard: 10.50 + 10.50 + 12.50 = 33.50
        var s1 = new SeatId(new RowNumber(1), new SeatNumber(1));
        var s2 = new SeatId(new RowNumber(1), new SeatNumber(2));
        var s3 = new SeatId(new RowNumber(1), new SeatNumber(3));
        var seatingPlan = new SeatingPlan(screeningId, List.of(
                new Seat(s1, SeatCategory.STALLS, false),
                new Seat(s2, SeatCategory.STALLS, false),
                new Seat(s3, SeatCategory.BOX, false)));
        var screening = screening(ScreeningCategory.STANDARD);

        // act
        var price = priceCalculation.calculatePrice(screening, seatingPlan, new AdjacentSeats(List.of(s1, s2, s3)));

        // assert
        assertThat(price).isEqualTo(MoneyAmount.euro(33, 50));
    }

    private Screening screening(ScreeningCategory category) {
        return new Screening(
                screeningId,
                new Hall(LocalizedText.of("kleiner Saal", "Small Hall")),
                new StartTime(LocalDateTime.parse("2025-03-19T14:30")),
                new Film("Guardians of the Lunacy"),
                category);
    }
}
