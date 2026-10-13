package de.wps.ddd.cinema.ticketsale.adapters.persistence.mappers;

import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers.TicketEntityMapper;
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
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketEntityMapperTest {

    private static final LocalizedText SMALL_HALL = LocalizedText.of("kleiner Saal", "Small Hall");

    private final TicketEntityMapper mapper = new TicketEntityMapper();

    private final TicketId ticketId =
            new TicketId(UUID.fromString("2b7a2a4d-6d3e-4d0a-9a3f-2b1d5e0c9a11"));
    private final OrderNumber orderNumber =
            new OrderNumber(UUID.fromString("a095c8f6-6fa2-4f2e-acf1-52cee0698e74"));
    private final ScreeningId screeningId =
            new ScreeningId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90"));
    private final LocalDateTime start = LocalDateTime.parse("2025-03-23T14:30");

    @Test
    void toEntity() {
        // arrange
        var ticket = new Ticket(ticketId, orderNumber, screeningId,
                new Film("The Fast and the Curious"), new StartTime(start), new Hall(SMALL_HALL),
                new RowNumber(4), new SeatNumber(2));

        // act
        var entity = mapper.toEntity(ticket);

        // assert
        assertThat(entity.getTicketId()).isEqualTo(ticketId.value());
        assertThat(entity.getOrderNumber()).isEqualTo(orderNumber.value());
        assertThat(entity.getScreeningId()).isEqualTo(screeningId.value());
        assertThat(entity.getFilmName()).isEqualTo("The Fast and the Curious");
        assertThat(entity.getStart()).isEqualTo(start);
        assertThat(entity.getHallName()).isEqualTo(SMALL_HALL);
        assertThat(entity.getRowNumber()).isEqualTo(4);
        assertThat(entity.getSeatNumber()).isEqualTo(2);
    }

    @Test
    void toDomain() {
        // arrange
        var entity = new TicketEntity(ticketId.value(), orderNumber.value(), screeningId.value(),
                "The Fast and the Curious", start, SMALL_HALL, 4, 2);

        // act
        var ticket = mapper.toDomain(entity);

        // assert
        assertThat(ticket.getId()).isEqualTo(ticketId);
        assertThat(ticket.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(ticket.getScreeningId()).isEqualTo(screeningId);
        assertThat(ticket.getFilm()).isEqualTo(new Film("The Fast and the Curious"));
        assertThat(ticket.getStart()).isEqualTo(new StartTime(start));
        assertThat(ticket.getHall()).isEqualTo(new Hall(SMALL_HALL));
        assertThat(ticket.getRow()).isEqualTo(new RowNumber(4));
        assertThat(ticket.getSeat()).isEqualTo(new SeatNumber(2));
    }
}
