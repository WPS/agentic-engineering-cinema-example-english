package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningCategory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketBlockTest {

    private final TicketBlock ticketBlock = new TicketBlock();

    private static Screening screening() {
        return new Screening(
                new ScreeningId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90")),
                new Hall(LocalizedText.of("kleiner Saal", "Small Hall")),
                new StartTime(LocalDateTime.parse("2025-03-23T14:30")),
                new Film("The Fast and the Curious"),
                ScreeningCategory.STANDARD);
    }

    @Test
    void createTickets_createsOneTicketPerSeatWithScreeningData() {
        // arrange
        var screening = screening();
        var s1 = new SeatId(new RowNumber(4), new SeatNumber(1));
        var s2 = new SeatId(new RowNumber(4), new SeatNumber(2));
        var selectedSeats = new AdjacentSeats(List.of(s1, s2));
        var orderNumber = OrderNumber.generate();

        // act
        var tickets = ticketBlock.createTickets(orderNumber, screening, selectedSeats);

        // assert
        assertThat(tickets).hasSize(2);
        assertThat(tickets).allSatisfy(ticket -> {
            assertThat(ticket.getId()).isNotNull();
            assertThat(ticket.getOrderNumber()).isEqualTo(orderNumber);
            assertThat(ticket.getScreeningId()).isEqualTo(screening.getId());
            assertThat(ticket.getFilm()).isEqualTo(screening.getFilm());
            assertThat(ticket.getStart()).isEqualTo(screening.getStart());
            assertThat(ticket.getHall()).isEqualTo(screening.getHall());
            assertThat(ticket.getRow()).isEqualTo(new RowNumber(4));
        });
        assertThat(tickets).extracting(ticket -> ticket.getSeat().value()).containsExactly(1, 2);
    }

    @Test
    void createTickets_givesEachTicketItsOwnTicketId() {
        // arrange
        var selectedSeats = new AdjacentSeats(List.of(
                new SeatId(new RowNumber(4), new SeatNumber(1)),
                new SeatId(new RowNumber(4), new SeatNumber(2))));

        // act
        var tickets = ticketBlock.createTickets(OrderNumber.generate(), screening(), selectedSeats);

        // assert
        assertThat(tickets).extracting(Ticket::getId).doesNotHaveDuplicates();
    }
}
