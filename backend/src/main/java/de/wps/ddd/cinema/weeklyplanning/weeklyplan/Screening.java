package de.wps.ddd.cinema.weeklyplanning.weeklyplan;

import de.wps.ddd.cinema.weeklyplanning.filmcatalog.Film;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.FilmTitle;
import de.wps.ddd.cinema.weeklyplanning.hallmanagement.HallName;
import org.jmolecules.ddd.annotation.Association;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A screening scheduled in the weekly plan — an entity with its own identity ({@link ScreeningId})
 * inside the {@link WeeklyPlan} aggregate. References film and hall only by their identities
 * ({@link FilmTitle} / {@link HallName}), not by the aggregates themselves.
 */
@Entity
public record Screening(
        @Identity
        ScreeningId id,
        @Association(aggregateType = Film.class)
        FilmTitle film,
        HallName hall,
        ScreeningCategory category,
        ScreeningTimes times) {

    public static Screening schedule(Film film, HallName hall, ScreeningCategory category, LocalDateTime start) {
        return new Screening(
                new ScreeningId(UUID.randomUUID()),
                film.title(),
                hall,
                category,
                ScreeningTimes.schedule(start, film.runtimeMinutes()));
    }
}
