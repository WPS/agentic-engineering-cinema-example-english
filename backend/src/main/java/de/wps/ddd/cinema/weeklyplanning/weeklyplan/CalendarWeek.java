package de.wps.ddd.cinema.weeklyplanning.weeklyplan;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record CalendarWeek(int year, int week) {
}
