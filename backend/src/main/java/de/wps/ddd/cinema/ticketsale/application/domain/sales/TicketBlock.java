package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import org.jmolecules.ddd.annotation.Factory;
import org.springframework.stereotype.Component;

import java.util.List;

@Factory
@Component
public class TicketBlock {
    public List<Ticket> createTickets(
            OrderNumber orderNumber,
            Screening screening,
            AdjacentSeats selectedSeats) {
        return selectedSeats.seats().stream().map(seatId -> new Ticket(
                TicketId.generate(),
                orderNumber,
                screening.getId(),
                screening.getFilm(),
                screening.getStart(),
                screening.getHall(),
                seatId.row(),
                seatId.number()
        )).toList();
    }
}
