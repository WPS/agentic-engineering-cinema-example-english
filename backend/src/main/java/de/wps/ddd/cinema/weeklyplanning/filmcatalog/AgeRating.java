package de.wps.ddd.cinema.weeklyplanning.filmcatalog;

import org.jmolecules.ddd.annotation.ValueObject;

/**
 * Age rating according to the German FSK (Freiwillige Selbstkontrolle der Filmwirtschaft); {@link #value()}
 * is the minimum age in years.
 */
@ValueObject
public enum AgeRating {
    FSK0(0),
    FSK6(6),
    FSK12(12),
    FSK16(16),
    FSK18(18);

    private final int value;

    AgeRating(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
