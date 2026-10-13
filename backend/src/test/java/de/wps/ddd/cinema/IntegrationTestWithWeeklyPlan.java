package de.wps.ddd.cinema;

import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.filmselection.service.ProgramService;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Seat;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.CurrentScreenings;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base class for integration tests that need the weekly plan published at startup via the
 * {@code WeeklyPlanFixture}. Since film selection and ticket sale obtain their views asynchronously
 * ({@code @ApplicationModuleListener} after commit), the base class waits (Awaitility) until they are
 * populated before the actual test runs.
 * <p>
 * {@link FixedClockTestConfig} pins the application clock to the reference day 2025-03-19 (CW 12/2025),
 * so the {@code WeeklyPlanFixture} deterministically creates the same schedule and the subclasses' date
 * literals stay stable.
 */
@Import(FixedClockTestConfig.class)
public abstract class IntegrationTestWithWeeklyPlan {

    protected static final LocalizedText SMALL_HALL = LocalizedText.of("kleiner Saal", "Small Hall");
    protected static final LocalizedText LARGE_HALL = LocalizedText.of("großer Saal", "Large Hall");

    private static final int EXPECTED_SCREENING_COUNT = 24;
    private static final LocalDate DAY_WITH_SCREENINGS = LocalDate.of(2025, 3, 19);

    @Autowired
    private CurrentScreenings currentScreenings;

    @Autowired
    private SeatingPlans seatingPlans;

    @Autowired
    private ProgramService programService;

    @BeforeEach
    void awaitWeeklyPlanAdoption() {
        Awaitility.await()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofMillis(200))
                .until(this::weeklyPlanAdopted);
    }

    private boolean weeklyPlanAdopted() {
        var screenings = currentScreenings.allScreenings();
        return screenings.size() >= EXPECTED_SCREENING_COUNT
                && !programService.getFilmsForDay(DAY_WITH_SCREENINGS).isEmpty()
                && occupancyApplied(screenings.get(0).getId());
    }

    /**
     * Signals that the (async) SeatingPlanOccupancyFixture is done: it commits its occupancy atomically,
     * so one sold seat on a SeatingPlan means "occupancy complete".
     */
    private boolean occupancyApplied(ScreeningId screeningId) {
        return seatingPlans.getSeatingPlan(screeningId).getSeats().values().stream()
                .flatMap(row -> row.values().stream())
                .anyMatch(Seat::isSold);
    }

    /**
     * Returns the {@link ScreeningId} of the screening with the given film, hall and start time.
     * Replaces the UUIDs that used to be hard-wired in {@code data.sql}.
     */
    protected ScreeningId screeningId(String film, LocalizedText hall, LocalDateTime start) {
        return currentScreenings.find(new Film(film), new Hall(hall), new StartTime(start))
                .map(Screening::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "No screening found for " + film + " / " + hall + " / " + start));
    }
}
