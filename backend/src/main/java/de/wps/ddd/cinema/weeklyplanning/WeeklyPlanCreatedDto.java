package de.wps.ddd.cinema.weeklyplanning;

import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent
public record WeeklyPlanCreatedDto(int year, int calendarWeek) {
}
