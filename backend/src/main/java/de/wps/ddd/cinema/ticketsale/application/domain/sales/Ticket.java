package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
@Getter
@AllArgsConstructor
public class Ticket {
    @Identity
    private final TicketId id;
    private final OrderNumber orderNumber;
    private final ScreeningId screeningId;
    private final Film film;
    private final StartTime start;
    private final Hall hall;
    private final RowNumber row;
    private final SeatNumber seat;
}
