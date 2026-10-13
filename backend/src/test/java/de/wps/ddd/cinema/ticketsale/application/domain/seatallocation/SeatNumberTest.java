package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SeatNumberTest {

    @Test
    void compareTo_comparesNumerically() {
        // act / assert
        assertThat(new SeatNumber(1)).isLessThan(new SeatNumber(2));
        assertThat(new SeatNumber(5)).isGreaterThan(new SeatNumber(2));
        assertThat(new SeatNumber(3)).isEqualByComparingTo(new SeatNumber(3));
    }
}
