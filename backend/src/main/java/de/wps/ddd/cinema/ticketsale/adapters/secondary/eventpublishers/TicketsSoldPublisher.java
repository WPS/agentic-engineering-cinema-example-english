package de.wps.ddd.cinema.ticketsale.adapters.secondary.eventpublishers;

import de.wps.ddd.cinema.ticketsale.TicketsSoldDto;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.TicketsSold;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SalesNotifications;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketsSoldPublisher implements SalesNotifications {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(TicketsSold ticketsSold) {
        eventPublisher.publishEvent(toDto(ticketsSold));
    }

    private TicketsSoldDto toDto(TicketsSold ticketsSold) {
        var screening = ticketsSold.screening();
        var soldTickets = ticketsSold.tickets().stream()
                .map(ticket -> new TicketsSoldDto.SoldTicket(
                        ticket.getId().value(), ticket.getRow().value(), ticket.getSeat().value()))
                .toList();
        return new TicketsSoldDto(
                ticketsSold.orderNumber().value(),
                screening.getId().value(),
                screening.getFilm().name(),
                screening.getStart().value(),
                screening.getHall().name(),
                soldTickets);
    }
}
