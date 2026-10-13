package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.common.architecture.ApplicationService;

@ApplicationService
public interface ImportWeeklyPlan {

    /** Imports the screenings of the given calendar week and creates empty seating plans for new ones. */
    void execute(int year, int calendarWeek);
}
