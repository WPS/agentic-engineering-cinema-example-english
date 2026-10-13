package de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.TicketDto;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketDtoMapper {

    public List<TicketDto> toDto(List<Ticket> tickets) {
        return tickets.stream().map(this::toDto).toList();
    }

    public TicketDto toDto(Ticket ticket) {
        return new TicketDto(
                ticket.getFilm().name(),
                ticket.getStart().value().toString(),
                ticket.getHall().name(),
                ticket.getRow().value(),
                ticket.getSeat().value()
        );
    }
}
