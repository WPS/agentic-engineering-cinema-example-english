package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.common.architecture.ApplicationService;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;

@ApplicationService
public interface StartSaleTransaction {
    /** Starts a sale transaction for the selected seats, fixing its total price. */
    SaleTransaction execute(ScreeningId screeningId, AdjacentSeats selectedSeats);
}
