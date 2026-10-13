package de.wps.ddd.cinema.ticketsale.application.fixtures;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.WeeklyPlanAdopted;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.CurrentScreenings;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * After the {@link WeeklyPlanAdopted} event, adds a demo occupancy to the seating plans (created empty
 * by the import): marks ~25 % of the seats as sold. Pure seed/demo data — deliberately separated from
 * the import, which only transforms the data it receives.
 * <p>
 * Runs async after the import has committed (the seating plans exist by then) in its own transaction.
 * The fixed seed makes the occupancy deterministic and reproducible.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SeatingPlanOccupancyFixture {

    private static final long OCCUPANCY_SEED = 42;

    private final CurrentScreenings currentScreenings;
    private final SeatingPlans seatingPlans;

    @ApplicationModuleListener
    public void on(WeeklyPlanAdopted event) {
        var screenings = currentScreenings.allScreenings();
        log.info("Creating demo occupancy for {} seating plans...", screenings.size());

        for (var screening : screenings) {
            var seatingPlan = seatingPlans.getSeatingPlan(screening.getId());
            occupy(seatingPlan);
            seatingPlans.putBack(seatingPlan);
        }
    }

    private void occupy(SeatingPlan seatingPlan) {
        var random = new Random(OCCUPANCY_SEED);
        seatingPlan.getSeats().values().forEach(row -> row.values().forEach(seat -> {
            if (random.nextInt(4) == 0) {
                seat.markAsSold();
            }
        }));
    }
}
