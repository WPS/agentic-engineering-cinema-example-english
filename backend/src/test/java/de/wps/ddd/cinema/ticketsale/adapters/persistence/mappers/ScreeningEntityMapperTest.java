package de.wps.ddd.cinema.ticketsale.adapters.persistence.mappers;

import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers.ScreeningEntityMapper;
import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.ScreeningEntity;
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

class ScreeningEntityMapperTest {

    private static final LocalizedText SMALL_HALL = LocalizedText.of("kleiner Saal", "Small Hall");

    private final ScreeningEntityMapper mapper = new ScreeningEntityMapper();

    private final UUID id = UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90");
    private final LocalDateTime start = LocalDateTime.parse("2025-03-23T14:30");

    @Test
    void toDomain() {
        // arrange
        var entity = new ScreeningEntity(id, start, SMALL_HALL, "The Fast and the Curious", "PRIME_TIME");

        // act
        var screening = mapper.toDomain(entity);

        // assert
        assertThat(screening.getId()).isEqualTo(new ScreeningId(id));
        assertThat(screening.getStart()).isEqualTo(new StartTime(start));
        assertThat(screening.getHall()).isEqualTo(new Hall(SMALL_HALL));
        assertThat(screening.getFilm()).isEqualTo(new Film("The Fast and the Curious"));
        assertThat(screening.getCategory()).isEqualTo(ScreeningCategory.PRIME_TIME);
    }

    @Test
    void toEntity() {
        // arrange
        var screening = new Screening(new ScreeningId(id), new Hall(SMALL_HALL), new StartTime(start),
                new Film("The Fast and the Curious"), ScreeningCategory.CHILDREN);

        // act
        var entity = mapper.toEntity(screening);

        // assert
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getStart()).isEqualTo(start);
        assertThat(entity.getHall()).isEqualTo(SMALL_HALL);
        assertThat(entity.getFilm()).isEqualTo("The Fast and the Curious");
        assertThat(entity.getCategory()).isEqualTo("CHILDREN");
    }
}
