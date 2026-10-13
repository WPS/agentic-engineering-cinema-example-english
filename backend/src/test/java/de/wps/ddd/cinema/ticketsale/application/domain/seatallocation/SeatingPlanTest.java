package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SeatingPlanTest {

    private final ScreeningId screeningId = new ScreeningId(UUID.fromString("a095c8f6-6fa2-4f2e-acf1-52cee0698e74"));

    @Test
    void findAdjacentSeats_adjacentSeatsInSecondToLastRow_returnsSeats() {
        // arrange
        var requestedSeatCount = 4;
        var secondToLastRow = 3;
        var seatingPlan = new SeatingPlan(screeningId, List.of(
                // row 1 with 4 free seats
                free(1, 1), free(1, 2), free(1, 3), free(1, 4), sold(1, 5), sold(1, 6),
                // row 2 without free seats
                sold(2, 1), sold(2, 2), sold(2, 3), sold(2, 4), sold(2, 5), sold(2, 6),
                // row 3 with 4 free seats -> match
                free(3, 1), free(3, 2), free(3, 3), free(3, 4), sold(3, 5), sold(3, 6),
                // row 4 with 4 free seats that are not adjacent
                free(4, 1), free(4, 2), free(4, 3), sold(4, 4), free(4, 5), sold(4, 6)));

        // act
        var adjacentSeats = seatingPlan.findAdjacentSeats(new SeatCount(requestedSeatCount));

        // assert
        assertThat(adjacentSeats.seats()).hasSize(requestedSeatCount);
        assertThat(adjacentSeats.seats()).allMatch(s -> s.row().value() == secondToLastRow);
        var seatNumbers = adjacentSeats.seats().stream().map(s -> s.number().value()).toList();
        assertThat(seatNumbers.getLast() - seatNumbers.getFirst()).isEqualTo(requestedSeatCount - 1);
    }

    @Test
    void findAdjacentSeats_noAdjacentSeats_returnsEmptyList() {
        // arrange — only isolated free seats, no two adjacent
        var seatingPlan = new SeatingPlan(screeningId, List.of(
                free(1, 1), sold(1, 2), sold(1, 3), sold(1, 4), sold(1, 5), sold(1, 6),
                sold(2, 1), sold(2, 2), sold(2, 3), free(2, 4), sold(2, 5), sold(2, 6)));

        // act
        var adjacentSeats = seatingPlan.findAdjacentSeats(new SeatCount(2));

        // assert
        assertThat(adjacentSeats.seats()).isEmpty();
    }

    @Test
    void findAdjacentSeats_noSeatsRequested_throwsException() {
        // arrange
        var seatingPlan = new SeatingPlan(screeningId, List.of(free(1, 1), free(1, 2), free(1, 3)));

        // act / assert
        assertThatThrownBy(() -> seatingPlan.findAdjacentSeats(new SeatCount(0)))
                .isInstanceOf(BusinessRuleViolated.class)
                .hasMessageContaining("At least one seat must be requested");
    }

    @Test
    void markAsSold_marksAdjacentSeatsAsSold() {
        // arrange
        var seatingPlan = new SeatingPlan(screeningId, List.of(free(1, 1), free(1, 2), free(1, 3)));
        var toSell = new AdjacentSeats(List.of(seatId(1, 1), seatId(1, 2), seatId(1, 3)));

        // act
        seatingPlan.markAsSold(toSell);

        // assert
        assertThat(seatingPlan.seat(seatId(1, 1)).isSold()).isTrue();
        assertThat(seatingPlan.seat(seatId(1, 2)).isSold()).isTrue();
        assertThat(seatingPlan.seat(seatId(1, 3)).isSold()).isTrue();
    }

    @Test
    void empty_frontHalfStallsBackHalfBox_allFree() {
        // act
        var seatingPlan = SeatingPlan.empty(screeningId, 4, 8);

        // assert
        assertThat(seatingPlan.getSeats()).hasSize(4);
        assertThat(seatingPlan.getSeats().values()).allSatisfy(row -> assertThat(row).hasSize(8));
        assertThat(seatingPlan.seat(seatId(2, 1)).getCategory()).isEqualTo(SeatCategory.STALLS);
        assertThat(seatingPlan.seat(seatId(3, 1)).getCategory()).isEqualTo(SeatCategory.BOX);
        assertThat(seatingPlan.getSeats().values()).allSatisfy(row ->
                assertThat(row.values()).allMatch(Seat::isFree));
    }

    private static Seat free(int row, int number) {
        return new Seat(seatId(row, number), SeatCategory.BOX, false);
    }

    private static Seat sold(int row, int number) {
        return new Seat(seatId(row, number), SeatCategory.BOX, true);
    }

    private static SeatId seatId(int row, int number) {
        return new SeatId(new RowNumber(row), new SeatNumber(number));
    }
}
