package de.wps.ddd.cinema.ticketsale.adapters.persistence.repositories;

import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.Ticket;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.TicketId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.Tickets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TicketsIntegrationTest {

    private static final LocalizedText SMALL_HALL = LocalizedText.of("kleiner Saal", "Small Hall");

    @Autowired
    private Tickets tickets;

    private final ScreeningId screeningId = new ScreeningId(UUID.randomUUID());

    @Test
    void saveAndFind_readsTheTicketsOfAnOrder() {
        // arrange
        var orderNumber = OrderNumber.generate();
        var orderTickets = List.of(ticket(orderNumber, 4, 1), ticket(orderNumber, 4, 2));

        // act
        tickets.save(orderTickets);
        var found = tickets.find(orderNumber);

        // assert
        assertThat(found).hasSize(2);
        assertThat(found).allSatisfy(ticket -> {
            assertThat(ticket.getOrderNumber()).isEqualTo(orderNumber);
            assertThat(ticket.getScreeningId()).isEqualTo(screeningId);
            assertThat(ticket.getFilm()).isEqualTo(new Film("The Fast and the Curious"));
            assertThat(ticket.getHall()).isEqualTo(new Hall(SMALL_HALL));
            assertThat(ticket.getRow()).isEqualTo(new RowNumber(4));
        });
        assertThat(found).extracting(ticket -> ticket.getSeat().value())
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void find_unknownOrderNumber_returnsEmptyList() {
        // arrange
        var unknownOrderNumber = OrderNumber.generate();

        // act / assert
        assertThat(tickets.find(unknownOrderNumber)).isEmpty();
    }

    private Ticket ticket(OrderNumber orderNumber, int row, int seat) {
        return new Ticket(
                TicketId.generate(),
                orderNumber,
                screeningId,
                new Film("The Fast and the Curious"),
                new StartTime(LocalDateTime.parse("2025-03-23T14:30")),
                new Hall(SMALL_HALL),
                new RowNumber(row),
                new SeatNumber(seat));
    }
}
