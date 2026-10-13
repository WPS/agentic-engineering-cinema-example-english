package de.wps.ddd.cinema.ticketsale.application.domain.screenings;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Film(String name) {
}
