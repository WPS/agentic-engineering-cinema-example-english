package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;

public interface CalculateTotalPrice {
    /** Calculates the total price of the given seats for the given screening. */
    MoneyAmount execute(ScreeningId screeningId, AdjacentSeats adjacentSeats);
}
