package de.wps.ddd.cinema.ticketsale.adapters.web.mappers;

import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.ScreeningDtoMapper;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningCategory;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ScreeningDtoMapperTest {

    private static final LocalizedText SMALL_HALL = LocalizedText.of("kleiner Saal", "Small Hall");

    private final ScreeningDtoMapper mapper = new ScreeningDtoMapper();

    @Test
    void toDto_mapsScreeningToDto() {
        // arrange
        var id = UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90");
        var screening = new Screening(
                new ScreeningId(id),
                new Hall(SMALL_HALL),
                new StartTime(LocalDateTime.parse("2025-03-23T14:30")),
                new Film("The Fast and the Curious"),
                ScreeningCategory.STANDARD);

        // act
        var dto = mapper.toDto(screening);

        // assert
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.start()).isEqualTo("2025-03-23T14:30");
        assertThat(dto.hall()).isEqualTo(SMALL_HALL);
        assertThat(dto.film()).isEqualTo("The Fast and the Curious");
    }
}
