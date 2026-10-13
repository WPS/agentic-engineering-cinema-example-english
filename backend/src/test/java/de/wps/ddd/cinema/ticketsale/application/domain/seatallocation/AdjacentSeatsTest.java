package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdjacentSeatsTest {

    @Test
    void count_equalsNumberOfSeats() {
        // arrange
        var seats = new AdjacentSeats(List.of(seatId(1, 1), seatId(1, 2), seatId(1, 3)));

        // act / assert
        assertThat(seats.count()).isEqualTo(new SeatCount(3));
    }

    @Test
    void count_noSeats_isZero() {
        // arrange
        var seats = new AdjacentSeats(List.of());

        // act / assert
        assertThat(seats.count()).isEqualTo(new SeatCount(0));
    }

    private static SeatId seatId(int row, int number) {
        return new SeatId(new RowNumber(row), new SeatNumber(number));
    }
}
