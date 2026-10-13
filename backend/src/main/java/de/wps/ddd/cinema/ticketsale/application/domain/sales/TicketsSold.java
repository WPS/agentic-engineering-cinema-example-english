package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import org.jmolecules.event.annotation.DomainEvent;

import java.util.List;

@DomainEvent
public record TicketsSold(OrderNumber orderNumber, Screening screening,
                          List<Ticket> tickets) {
}
