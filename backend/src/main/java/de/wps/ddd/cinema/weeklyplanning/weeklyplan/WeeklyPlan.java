package de.wps.ddd.cinema.weeklyplanning.weeklyplan;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;

/**
 * The weekly plan as an aggregate, identified by its {@link CalendarWeek}. Holds the screenings
 * scheduled for one week.
 */
@AggregateRoot
public class WeeklyPlan {

    @Identity
    private final CalendarWeek calendarWeek;
    private final List<Screening> screenings;

    private WeeklyPlan(CalendarWeek calendarWeek) {
        this.calendarWeek = calendarWeek;
        this.screenings = new ArrayList<>();
    }

    public static WeeklyPlan forWeek(CalendarWeek calendarWeek) {
        return new WeeklyPlan(calendarWeek);
    }

    public void scheduleScreening(Screening screening) {
        screenings.add(screening);
    }

    public CalendarWeek getCalendarWeek() {
        return calendarWeek;
    }

    public List<Screening> getScreenings() {
        return List.copyOf(screenings);
    }
}
