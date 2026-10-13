package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.HallName;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.WeeklyPlanAdopted;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.CurrentScreenings;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.Halls;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.WeeklyPlans;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.WeeklyPlanNotifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * After the {@code WeeklyPlanCreatedDto}, adopts weekly planning's weekly plan into the ticket sale:
 * obtains the screenings — already translated into its own domain — via the {@link WeeklyPlans}
 * secondary port, persists them and creates an (initially empty) SeatingPlan for each one from the
 * {@link Halls ticket sale's own seating layout} of its hall. Afterwards it publishes
 * {@link WeeklyPlanAdopted}, upon which the {@code SeatingPlanOccupancyFixture} adds the demo occupancy.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ImportWeeklyPlan implements de.wps.ddd.cinema.ticketsale.application.ports.primary.ImportWeeklyPlan {

    private final WeeklyPlans weeklyPlans;
    private final Halls halls;
    private final CurrentScreenings currentScreenings;
    private final SeatingPlans seatingPlans;
    private final WeeklyPlanNotifications weeklyPlanNotifications;

    @Override
    public void execute(int year, int calendarWeek) {
        var screenings = weeklyPlans.getWeeklyPlan(year, calendarWeek);
        log.info("Ticket sale imports weekly plan CW {}/{}: {} screenings", calendarWeek, year, screenings.size());

        for (var screening : screenings) {
            currentScreenings.save(screening);
            if (!seatingPlans.seatingPlanExists(screening.getId())) {
                var hallName = screening.getHall().name();
                var hall = halls.find(new HallName(hallName)).orElse(null);
                ResourceNotFound.throwIf(hall == null, "Hall " + hallName.en() + " is unknown");
                seatingPlans.putBack(SeatingPlan.empty(screening.getId(), hall.rows(), hall.seatsPerRow()));
            }
        }

        weeklyPlanNotifications.publish(new WeeklyPlanAdopted(year, calendarWeek));
    }
}
