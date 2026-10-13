package de.wps.ddd.cinema.ticketsale.application.domain.screenings;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum ScreeningCategory {
    STANDARD,
    PRIME_TIME,
    CHILDREN
}
