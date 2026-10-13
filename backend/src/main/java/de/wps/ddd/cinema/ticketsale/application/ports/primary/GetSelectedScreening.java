package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;

public interface GetSelectedScreening {
    /** Returns the screening the visitor has selected. */
    Screening execute(ScreeningId screeningId);
}
