package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SeatTest {

    private final SeatId seatId = new SeatId(new RowNumber(1), new SeatNumber(1));

    @Test
    void newSeat_notSold_isFree() {
        // act
        var seat = new Seat(seatId, SeatCategory.STALLS, false);

        // assert
        assertThat(seat.getId()).isEqualTo(seatId);
        assertThat(seat.getCategory()).isEqualTo(SeatCategory.STALLS);
        assertThat(seat.isSold()).isFalse();
        assertThat(seat.isFree()).isTrue();
    }

    @Test
    void newSeat_alreadySold_isNotFree() {
        // act
        var seat = new Seat(seatId, SeatCategory.BOX, true);

        // assert
        assertThat(seat.getId()).isEqualTo(seatId);
        assertThat(seat.getCategory()).isEqualTo(SeatCategory.BOX);
        assertThat(seat.isSold()).isTrue();
        assertThat(seat.isFree()).isFalse();
    }

    @Test
    void markAsSold_makesSeatSold() {
        // arrange
        var seat = new Seat(seatId, SeatCategory.BOX, false);

        // act
        seat.markAsSold();

        // assert
        assertThat(seat.isSold()).isTrue();
        assertThat(seat.isFree()).isFalse();
    }
}
