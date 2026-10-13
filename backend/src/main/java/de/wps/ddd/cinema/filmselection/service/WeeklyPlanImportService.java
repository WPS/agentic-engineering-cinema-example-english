package de.wps.ddd.cinema.filmselection.service;

import de.wps.ddd.cinema.common.architecture.ApplicationService;
import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.filmselection.data.CurrentFilms;
import de.wps.ddd.cinema.filmselection.data.Film;
import de.wps.ddd.cinema.filmselection.data.Hall;
import de.wps.ddd.cinema.filmselection.data.Halls;
import de.wps.ddd.cinema.filmselection.data.Screening;
import de.wps.ddd.cinema.filmselection.data.Screenings;
import de.wps.ddd.cinema.weeklyplanning.WeeklyPlanDto;
import de.wps.ddd.cinema.weeklyplanning.WeeklyPlanQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * After the {@code WeeklyPlanCreatedDto}, adopts the weekly plan of weekly planning into the film
 * selection's own storage (anti-corruption layer). To do so it queries the exposed
 * {@link WeeklyPlanQuery} and maps the neutral {@link WeeklyPlanDto} onto the film selection's films,
 * halls and screenings; the screenings are grouped per film.
 */
@ApplicationService
@Service
@RequiredArgsConstructor
@Slf4j
public class WeeklyPlanImportService {

    private final WeeklyPlanQuery weeklyPlanQuery;
    private final CurrentFilms currentFilms;
    private final Halls halls;
    private final Screenings screenings;

    @Transactional
    public void importWeeklyPlan(int year, int calendarWeek) {
        var weeklyPlanDto = weeklyPlanQuery.getWeeklyPlan(year, calendarWeek);

        log.info("Film selection imports weekly plan CW {}/{}: {} screenings", calendarWeek, year, weeklyPlanDto.screenings().size());

        var screeningsByFilm = weeklyPlanDto.screenings().stream()
                .collect(Collectors.groupingBy(WeeklyPlanDto.ScreeningDto::filmTitle, LinkedHashMap::new, Collectors.toList()));

        for (WeeklyPlanDto.FilmDto filmDto : weeklyPlanDto.films()) {
            var film = adoptFilm(filmDto);
            for (WeeklyPlanDto.ScreeningDto screeningDto : screeningsByFilm.getOrDefault(filmDto.title(), List.of())) {
                adoptScreening(film, screeningDto);
            }
        }
    }

    private Film adoptFilm(WeeklyPlanDto.FilmDto filmDto) {
        var film = currentFilms.findByTitle(filmDto.title()).orElseGet(Film::new);
        film.setTitle(filmDto.title());
        film.setRuntimeMinutes(filmDto.runtimeMinutes());
        film.setPosterUrl(filmDto.posterUrl());
        film.setAgeRating(filmDto.ageRating());
        film.setDescription(filmDto.description());
        film.setGenre(filmDto.genre());
        film.setLeadActors(filmDto.leadActors());
        film.setDirector(filmDto.director());
        film.setLanguage(filmDto.language());
        return currentFilms.save(film);
    }

    private void adoptScreening(Film film, WeeklyPlanDto.ScreeningDto screeningDto) {
        var hall = findOrCreateHall(screeningDto.hallName());
        var screening = screenings.findByUuid(screeningDto.id()).orElseGet(Screening::new);
        screening.setUuid(screeningDto.id());
        screening.setFilmId(film.getId());
        screening.setStart(screeningDto.start());
        screening.setHall(hall);
        screenings.save(screening);
    }

    private Hall findOrCreateHall(LocalizedText name) {
        return halls.findByName(name).orElseGet(() -> halls.save(new Hall(null, name)));
    }
}
