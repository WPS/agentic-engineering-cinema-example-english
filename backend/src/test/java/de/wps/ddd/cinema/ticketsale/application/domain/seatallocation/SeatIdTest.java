package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SeatIdTest {

    @Test
    void equality_isBasedOnRowAndNumber() {
        // arrange
        var seatId = new SeatId(new RowNumber(4), new SeatNumber(2));

        // act / assert
        assertThat(seatId).isEqualTo(new SeatId(new RowNumber(4), new SeatNumber(2)));
        assertThat(seatId).isNotEqualTo(new SeatId(new RowNumber(4), new SeatNumber(3)));
        assertThat(seatId).isNotEqualTo(new SeatId(new RowNumber(3), new SeatNumber(2)));
    }
}
