package de.wps.ddd.cinema.ticketsale.application.ports.secondary;

import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.Ticket;
import org.jmolecules.ddd.annotation.Repository;

import java.util.List;

@Repository
public interface Tickets {

    /** Saves the issued tickets. */
    void save(List<Ticket> tickets);

    /** Finds the tickets of the order (empty if there are none). */
    List<Ticket> find(OrderNumber orderNumber);
}
