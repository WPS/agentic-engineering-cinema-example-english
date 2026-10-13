package de.wps.ddd.cinema.weeklyplanning.application;

import de.wps.ddd.cinema.common.architecture.ApplicationService;
import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.weeklyplanning.WeeklyPlanDto;
import de.wps.ddd.cinema.weeklyplanning.WeeklyPlanQuery;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.Film;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.FilmCatalog;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.FilmTitle;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.CalendarWeek;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.Screening;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.WeeklyPlan;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.WeeklyPlans;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Answers queries on the weekly plan (built by the fixture) and returns the neutral
 * {@link WeeklyPlanDto} view shared by both consumers (without planning internals). The weekly plan is
 * not created here but directly in the fixture.
 */
@Service
@ApplicationService
@RequiredArgsConstructor
public class WeeklyPlanQueryService implements WeeklyPlanQuery {

    private final FilmCatalog filmCatalog;
    private final WeeklyPlans weeklyPlans;

    @Override
    public WeeklyPlanDto getWeeklyPlan(int year, int calendarWeek) {
        var weeklyPlan = findWeeklyPlan(year, calendarWeek);

        var films = weeklyPlan.getScreenings().stream()
                .map(Screening::film)
                .distinct()
                .map(this::getFilm)
                .map(film -> new WeeklyPlanDto.FilmDto(film.title().value(), film.runtimeMinutes(), film.posterUrl(),
                        film.ageRating().value(), film.description(), film.genre(), film.leadActors(),
                        film.director(), film.language()))
                .toList();

        var screenings = weeklyPlan.getScreenings().stream()
                .map(screening -> new WeeklyPlanDto.ScreeningDto(screening.id().value(),
                        screening.film().value(), screening.hall().value(),
                        screening.category().name(), screening.times().start()))
                .toList();

        return new WeeklyPlanDto(films, screenings);
    }

    private Film getFilm(FilmTitle title) {
        return orNotFound(filmCatalog.find(title), "Film not in catalog: " + title.value());
    }

    private WeeklyPlan findWeeklyPlan(int year, int calendarWeek) {
        return orNotFound(weeklyPlans.find(new CalendarWeek(year, calendarWeek)),
                "No weekly plan for calendar week " + calendarWeek + "/" + year);
    }

    private <T> T orNotFound(Optional<T> found, String message) {
        ResourceNotFound.throwIf(found.isEmpty(), message);
        return found.get();
    }
}
