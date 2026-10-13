package de.wps.ddd.cinema.filmselection.service;

import de.wps.ddd.cinema.IntegrationTestWithWeeklyPlan;
import de.wps.ddd.cinema.common.text.LocalizedText;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class ProgramServiceIntegrationTest extends IntegrationTestWithWeeklyPlan {

    @Autowired
    private ProgramService programService;

    @Test
    void getFilmsForDay_threeFilmsOnTheDay_returnsFilms() {
        // arrange
        var date = LocalDate.parse("2025-03-19");

        // act
        var films = programService.getFilmsForDay(date);

        // assert
        assertThat(films).isNotNull();
        assertThat(films).hasSize(3);
        var film1 = films.get(0);
        var film2 = films.get(1);
        var film3 = films.get(2);
        assertThat(film1.getTitle()).isEqualTo("Guardians of the Lunacy");
        assertThat(film2.getTitle()).isEqualTo("The Fast and the Curious");
        assertThat(film3.getTitle()).isEqualTo("Clown Wars");
        assertThat(film1.getScreenings()).hasSize(1);
        assertThat(film2.getScreenings()).hasSize(2);
        assertThat(film3.getScreenings()).hasSize(1);
        assertThat(film1.getScreenings().get(0).getStart()).isEqualTo("2025-03-19T14:30:00");
        assertThat(film2.getScreenings().get(0).getStart()).isEqualTo("2025-03-19T15:00:00");
        assertThat(film2.getScreenings().get(1).getStart()).isEqualTo("2025-03-19T20:30:00");
        assertThat(film3.getScreenings().get(0).getStart()).isEqualTo("2025-03-19T22:30:00");
        assertThat(film1.getDescription().en()).isNotBlank();
        assertThat(film1.getScreenings().get(0).getHall().getName()).isIn(
                LocalizedText.of("großer Saal", "Large Hall"),
                LocalizedText.of("kleiner Saal", "Small Hall"));
    }

    @Test
    void getFilmsForDay_noScreeningsOnTheDay_returnsEmptyList() {
        // arrange
        var date = LocalDate.parse("2000-01-01");

        // act
        var films = programService.getFilmsForDay(date);

        // assert
        assertThat(films).isNotNull();
        assertThat(films).isEmpty();
    }
}
