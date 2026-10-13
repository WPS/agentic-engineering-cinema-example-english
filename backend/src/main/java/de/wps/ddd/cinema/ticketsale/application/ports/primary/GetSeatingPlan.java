package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;

public interface GetSeatingPlan {
    /** Returns the seating plan of the screening. */
    SeatingPlan execute(ScreeningId screeningId);
}
