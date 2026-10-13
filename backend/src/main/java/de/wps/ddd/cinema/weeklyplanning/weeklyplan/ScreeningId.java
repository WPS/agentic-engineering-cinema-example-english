package de.wps.ddd.cinema.weeklyplanning.weeklyplan;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record ScreeningId(UUID value) {
}
