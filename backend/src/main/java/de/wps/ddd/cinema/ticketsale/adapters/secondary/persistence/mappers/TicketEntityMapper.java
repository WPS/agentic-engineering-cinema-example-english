package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.TicketEntity;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.Ticket;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.TicketId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import org.springframework.stereotype.Component;

@Component
public class TicketEntityMapper {

    public TicketEntity toEntity(Ticket ticket) {
        return new TicketEntity(
                ticket.getId().value(),
                ticket.getOrderNumber().value(),
                ticket.getScreeningId().value(),
                ticket.getFilm().name(),
                ticket.getStart().value(),
                ticket.getHall().name(),
                ticket.getRow().value(),
                ticket.getSeat().value()
        );
    }

    public Ticket toDomain(TicketEntity ticketEntity) {
        return new Ticket(
                new TicketId(ticketEntity.getTicketId()),
                new OrderNumber(ticketEntity.getOrderNumber()),
                new ScreeningId(ticketEntity.getScreeningId()),
                new Film(ticketEntity.getFilmName()),
                new StartTime(ticketEntity.getStart()),
                new Hall(ticketEntity.getHallName()),
                new RowNumber(ticketEntity.getRowNumber()),
                new SeatNumber(ticketEntity.getSeatNumber())
        );
    }
}
