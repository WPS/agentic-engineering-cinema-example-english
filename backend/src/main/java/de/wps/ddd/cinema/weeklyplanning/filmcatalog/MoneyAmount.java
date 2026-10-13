package de.wps.ddd.cinema.weeklyplanning.filmcatalog;

import org.jmolecules.ddd.annotation.ValueObject;

/**
 * Weekly planning's own money amount (e.g. for distribution fees). Deliberately separate from ticket
 * sale's {@code MoneyAmount} — every context owns its own types.
 */
@ValueObject
public record MoneyAmount(long cents) {

    public static MoneyAmount euro(int euros, int cents) {
        return new MoneyAmount(euros * 100L + cents);
    }
}
