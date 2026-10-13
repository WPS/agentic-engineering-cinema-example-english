package de.wps.ddd.cinema.ticketsale.adapters.secondary.gateways;

import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningCategory;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.WeeklyPlans;
import de.wps.ddd.cinema.weeklyplanning.WeeklyPlanDto;
import de.wps.ddd.cinema.weeklyplanning.WeeklyPlanQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Secondary adapter (anti-corruption layer): queries the {@link WeeklyPlanQuery} exposed by weekly
 * planning and translates the screenings of the {@link WeeklyPlanDto} into the ticket-sale domain. The
 * seating layout is not part of the DTO — it comes from the ticket sale's own master data.
 */
@Component
@RequiredArgsConstructor
public class WeeklyPlanningGateway implements WeeklyPlans {

    private final WeeklyPlanQuery weeklyPlanQuery;

    @Override
    public List<Screening> getWeeklyPlan(int year, int calendarWeek) {
        return weeklyPlanQuery.getWeeklyPlan(year, calendarWeek).screenings().stream()
                .map(this::toScreening)
                .toList();
    }

    private Screening toScreening(WeeklyPlanDto.ScreeningDto dto) {
        return new Screening(
                new ScreeningId(dto.id()),
                new Hall(dto.hallName()),
                new StartTime(dto.start()),
                new Film(dto.filmTitle()),
                ScreeningCategory.valueOf(dto.category()));
    }
}
