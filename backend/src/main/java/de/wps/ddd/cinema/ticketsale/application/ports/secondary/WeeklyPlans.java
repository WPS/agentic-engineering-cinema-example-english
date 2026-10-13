package de.wps.ddd.cinema.ticketsale.application.ports.secondary;

import de.wps.ddd.cinema.common.architecture.Gateway;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;

import java.util.List;

/**
 * Secondary port (gateway) to weekly planning: provides the screenings of the calendar week already
 * translated into the ticket-sale domain. The adapter encapsulates the translation of the exposed
 * {@code WeeklyPlanDto} (anti-corruption layer). The seating layout does NOT come from here but from the
 * ticket sale's own master data ({@link Halls}).
 */
@Gateway
public interface WeeklyPlans {

    /** Returns the screenings of the given calendar week, translated into the ticket-sale domain. */
    List<Screening> getWeeklyPlan(int year, int calendarWeek);
}
