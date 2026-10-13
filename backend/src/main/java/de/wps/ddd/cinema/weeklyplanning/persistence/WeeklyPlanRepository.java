package de.wps.ddd.cinema.weeklyplanning.persistence;

import de.wps.ddd.cinema.weeklyplanning.weeklyplan.CalendarWeek;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.WeeklyPlan;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.WeeklyPlans;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WeeklyPlanRepository implements WeeklyPlans {

    private final ConcurrentHashMap<CalendarWeek, WeeklyPlan> weeklyPlans = new ConcurrentHashMap<>();

    @Override
    public Optional<WeeklyPlan> find(CalendarWeek calendarWeek) {
        return Optional.ofNullable(weeklyPlans.get(calendarWeek));
    }

    @Override
    public void save(WeeklyPlan weeklyPlan) {
        weeklyPlans.put(weeklyPlan.getCalendarWeek(), weeklyPlan);
    }
}
