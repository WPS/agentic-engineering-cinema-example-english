package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.IntegrationTestWithWeeklyPlan;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Seat;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.ImportWeeklyPlan;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.CurrentScreenings;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks that adopting the same weekly plan again does not overwrite existing seating plans — and thus
 * seats already sold. The import creates an empty SeatingPlan only for new screenings; known ones keep
 * their existing plan (including its occupancy).
 */
@SpringBootTest
class WeeklyPlanImportIntegrationTest extends IntegrationTestWithWeeklyPlan {

    @Autowired
    private ImportWeeklyPlan importWeeklyPlan;

    @Autowired
    private SeatingPlans seatingPlans;

    @Autowired
    private CurrentScreenings currentScreenings;

    @Test
    void execute_weeklyPlanAdoptedAgain_keepsSoldSeats() {
        // arrange: sell a seat that is still free (the deterministic demo occupancy leaves it free)
        var screeningId = currentScreenings.allScreenings().get(0).getId();
        var seatingPlan = seatingPlans.getSeatingPlan(screeningId);
        var freeSeat = firstFreeSeat(seatingPlan.getSeats().values().stream()
                .flatMap(row -> row.values().stream())
                .toList());
        seatingPlan.markAsSold(new AdjacentSeats(List.of(freeSeat.getId())));
        seatingPlans.putBack(seatingPlan);

        // act
        importWeeklyPlan.execute(2025, 12);

        // assert
        var afterImport = seatingPlans.getSeatingPlan(screeningId);
        assertThat(afterImport.seat(freeSeat.getId()).isSold()).isTrue();
    }

    @Test
    void execute_mapsBilingualHallNamesOntoTheHallsSeatingLayout() {
        // the small hall has 4 rows × 8 seats, the large hall 6 rows × 12 seats (HallsFixture)
        var small = seatingPlans.getSeatingPlan(
                screeningId("The Fast and the Curious", SMALL_HALL, LocalDateTime.of(2025, 3, 23, 14, 30)));
        assertThat(small.getSeats()).hasSize(4);
        assertThat(small.getSeats().firstEntry().getValue()).hasSize(8);

        var largeScreening = currentScreenings.allScreenings().stream()
                .filter(screening -> screening.getHall().name().equals(LARGE_HALL))
                .findFirst()
                .orElseThrow();
        var large = seatingPlans.getSeatingPlan(largeScreening.getId());
        assertThat(large.getSeats()).hasSize(6);
        assertThat(large.getSeats().firstEntry().getValue()).hasSize(12);
    }

    private Seat firstFreeSeat(List<Seat> seats) {
        return seats.stream()
                .filter(Seat::isFree)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No free seat in the seating plan"));
    }
}
