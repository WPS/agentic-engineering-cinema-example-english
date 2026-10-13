package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCount;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;

public interface FindAdjacentSeats {
    /** Finds the first {@code seatCount} free adjacent seats of the screening (empty if there are none). */
    AdjacentSeats execute(ScreeningId screeningId, SeatCount seatCount);
}
