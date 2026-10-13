package de.wps.ddd.cinema.filmselection.service;

import de.wps.ddd.cinema.IntegrationTestWithWeeklyPlan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that adopting the same weekly plan again does not duplicate the film selection's
 * screenings: the import is an idempotent upsert keyed by the weekly planning UUID.
 */
@SpringBootTest
class WeeklyPlanImportServiceIntegrationTest extends IntegrationTestWithWeeklyPlan {

    private static final LocalDate DAY_WITH_SCREENINGS = LocalDate.parse("2025-03-19");

    @Autowired
    private WeeklyPlanImportService weeklyPlanImportService;

    @Autowired
    private ProgramService programService;

    @Test
    void importWeeklyPlan_weeklyPlanAdoptedAgain_doesNotDuplicateScreenings() {
        // arrange
        var screeningsBefore = numberOfScreenings(DAY_WITH_SCREENINGS);

        // act
        weeklyPlanImportService.importWeeklyPlan(2025, 12);

        // assert
        assertThat(numberOfScreenings(DAY_WITH_SCREENINGS)).isEqualTo(screeningsBefore);
    }

    private int numberOfScreenings(LocalDate day) {
        return programService.getFilmsForDay(day).stream()
                .mapToInt(film -> film.getScreenings().size())
                .sum();
    }
}
