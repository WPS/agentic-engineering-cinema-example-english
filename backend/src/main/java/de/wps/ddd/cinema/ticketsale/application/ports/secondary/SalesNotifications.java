package de.wps.ddd.cinema.ticketsale.application.ports.secondary;

import de.wps.ddd.cinema.common.architecture.Gateway;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.TicketsSold;

@Gateway
public interface SalesNotifications {

    /** Publishes that tickets have been sold (to other modules, as {@code TicketsSoldDto}). */
    void publish(TicketsSold ticketsSold);
}
