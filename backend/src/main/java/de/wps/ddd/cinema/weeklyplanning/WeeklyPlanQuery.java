package de.wps.ddd.cinema.weeklyplanning;

/**
 * Exposed query API of weekly planning. After the {@link WeeklyPlanCreatedDto} event, film selection
 * and ticket sale fetch the {@link WeeklyPlanDto} through this interface and map it onto their own
 * structures (notification event + query back).
 */
public interface WeeklyPlanQuery {

    WeeklyPlanDto getWeeklyPlan(int year, int calendarWeek);
}
