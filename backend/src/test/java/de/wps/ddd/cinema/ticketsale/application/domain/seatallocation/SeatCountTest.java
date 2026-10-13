package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SeatCountTest {

    @Test
    void constructor_positiveValue_isAccepted() {
        // act
        var seatCount = new SeatCount(4);

        // assert
        assertThat(seatCount.value()).isEqualTo(4);
    }

    @Test
    void constructor_zero_isAccepted() {
        // act
        var seatCount = new SeatCount(0);

        // assert
        assertThat(seatCount.value()).isEqualTo(0);
    }

    @Test
    void constructor_negativeValue_throwsException() {
        // act / assert
        assertThatThrownBy(() -> new SeatCount(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seat count must not be negative");
    }
}
