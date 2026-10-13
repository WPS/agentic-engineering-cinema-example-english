package de.wps.ddd.cinema.weeklyplanning;

import de.wps.ddd.cinema.common.text.LocalizedText;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Neutral view of a weekly plan, shared by both consumers. Contains only data that may be exposed
 * (film master data, screenings with their start time and hall name) — no planning internals such as
 * distribution fees, licence periods or admission/cleanup times. The seating of the halls is not part
 * of weekly planning; it belongs to each consumer (ticket sale knows it itself).
 * <p>
 * Film selection and ticket sale translate this DTO into their own context (anti-corruption layer).
 */
public record WeeklyPlanDto(List<FilmDto> films, List<ScreeningDto> screenings) {

    public record FilmDto(
            String title,
            int runtimeMinutes,
            String posterUrl,
            int ageRating,
            LocalizedText description,
            LocalizedText genre,
            String leadActors,
            String director,
            LocalizedText language) {
    }

    /**
     * @param category the name of the screening category constant: {@code STANDARD}, {@code PRIME_TIME} or {@code CHILDREN}
     */
    public record ScreeningDto(UUID id, String filmTitle, LocalizedText hallName, String category, LocalDateTime start) {
    }
}
