package de.wps.ddd.cinema.weeklyplanning.weeklyplan;

import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;

@Repository
public interface WeeklyPlans {

    Optional<WeeklyPlan> find(CalendarWeek calendarWeek);

    void save(WeeklyPlan weeklyPlan);
}
