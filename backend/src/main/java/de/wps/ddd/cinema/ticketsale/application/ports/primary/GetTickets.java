package de.wps.ddd.cinema.ticketsale.application.ports.primary;

import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.Ticket;

import java.util.List;

public interface GetTickets {
    /** Returns the tickets sold for the order; fails if none have been sold. */
    List<Ticket> execute(OrderNumber orderNumber);
}
