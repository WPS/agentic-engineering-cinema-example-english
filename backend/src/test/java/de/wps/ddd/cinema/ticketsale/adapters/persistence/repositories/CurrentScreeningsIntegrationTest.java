package de.wps.ddd.cinema.ticketsale.adapters.persistence.repositories;

import de.wps.ddd.cinema.IntegrationTestWithWeeklyPlan;
import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.CurrentScreenings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CurrentScreeningsIntegrationTest extends IntegrationTestWithWeeklyPlan {

    @Autowired
    private CurrentScreenings currentScreenings;

    @Test
    void allScreenings_returnsScreenings() {
        // act
        var screenings = currentScreenings.allScreenings();

        // assert
        assertThat(screenings).isNotEmpty();
    }

    @Test
    void getScreening_knownScreening_returnsScreening() {
        // arrange
        var screeningId = screeningId("The Fast and the Curious", SMALL_HALL, LocalDateTime.of(2025, 3, 23, 14, 30));

        // act
        var screening = currentScreenings.getScreening(screeningId);

        // assert
        assertThat(screening.getId()).isEqualTo(screeningId);
        assertThat(screening.getFilm().name()).isEqualTo("The Fast and the Curious");
        assertThat(screening.getHall().name()).isEqualTo(SMALL_HALL);
    }

    @Test
    void find_hallMatchingInOneLanguageOnly_findsNothing() {
        // act — both languages of the hall name take part in the hall's identity
        var screening = currentScreenings.find(
                new Film("The Fast and the Curious"),
                new Hall(LocalizedText.of(SMALL_HALL.de(), LARGE_HALL.en())),
                new StartTime(LocalDateTime.of(2025, 3, 23, 14, 30)));

        // assert
        assertThat(screening).isEmpty();
    }

    @Test
    void getScreening_unknownScreening_throwsResourceNotFound() {
        // arrange
        var unknownScreeningId = new ScreeningId(UUID.randomUUID());

        // act / assert
        assertThatThrownBy(() -> currentScreenings.getScreening(unknownScreeningId))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("does not exist");
    }
}
