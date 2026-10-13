package de.wps.ddd.cinema.ticketsale.adapters.persistence.repositories;


import de.wps.ddd.cinema.IntegrationTestWithWeeklyPlan;
import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Seat;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCategory;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SeatingPlansIntegrationTest extends IntegrationTestWithWeeklyPlan {

    @Autowired
    private SeatingPlans seatingPlans;

    @Test
    void getSeatingPlan_knownScreening_returnsSeatingPlan() {
        // arrange
        var screeningId = screeningId("Star Boars", SMALL_HALL, LocalDateTime.of(2025, 3, 18, 15, 30));

        // act
        var seatingPlan = seatingPlans.getSeatingPlan(screeningId);

        // assert
        assertThat(seatingPlan).isNotNull();
        assertThat(seatingPlan.getScreeningId()).isEqualTo(screeningId);
        int rowCount = 4;
        int seatsPerRow = 8;
        List<RowNumber> rows = seatingPlan.getSeats().keySet().stream().toList();
        assertThat(rows).hasSize(rowCount);
        seatingPlan.getSeats().forEach((row, seats) -> assertThat(seats).hasSize(seatsPerRow));
    }

    @Test
    void getSeatingPlan_unknownScreening_throwsResourceNotFound() {
        // arrange
        var unknownScreeningId = new ScreeningId(UUID.randomUUID());

        // act / assert
        assertThatThrownBy(() -> seatingPlans.getSeatingPlan(unknownScreeningId))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void putBack_changedSeatingPlan() {
        // arrange
        var screeningId = screeningId("Star Boars", SMALL_HALL, LocalDateTime.of(2025, 3, 18, 15, 30));
        var seatingPlan = seatingPlans.getSeatingPlan(screeningId);
        var seatId = new SeatId(new RowNumber(2), new SeatNumber(3));
        assertThat(seatingPlan.seat(seatId).isSold()).isFalse();

        // act
        seatingPlan.seat(seatId).markAsSold();
        seatingPlans.putBack(seatingPlan);

        // assert
        var changedSeatingPlan = seatingPlans.getSeatingPlan(screeningId);
        assertThat(changedSeatingPlan.seat(seatId).isSold()).isTrue();
    }

    @Test
    void putBack_newSeatingPlan() {
        // arrange
        var screeningId = new ScreeningId(UUID.randomUUID());
        var seatId = new SeatId(new RowNumber(1), new SeatNumber(1));
        var seatCategory = SeatCategory.BOX;
        var seatingPlan = new SeatingPlan(screeningId, List.of(
                new Seat(seatId, seatCategory, true)));

        // act
        seatingPlans.putBack(seatingPlan);

        // assert
        var newSeatingPlan = seatingPlans.getSeatingPlan(screeningId);
        assertThat(newSeatingPlan).isNotNull();
        assertThat(newSeatingPlan.seat(seatId).getCategory()).isEqualTo(SeatCategory.BOX);
        assertThat(newSeatingPlan.seat(seatId).isSold()).isTrue();
    }
}
