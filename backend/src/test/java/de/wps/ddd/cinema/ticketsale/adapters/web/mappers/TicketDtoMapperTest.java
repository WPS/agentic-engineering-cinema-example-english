package de.wps.ddd.cinema.ticketsale.adapters.web.mappers;

import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.TicketDtoMapper;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketDtoMapperTest {

    private static final LocalizedText SMALL_HALL = LocalizedText.of("kleiner Saal", "Small Hall");

    private final TicketDtoMapper mapper = new TicketDtoMapper();

    private Ticket ticket(int row, int seat) {
        return new Ticket(
                TicketId.generate(),
                OrderNumber.generate(),
                new ScreeningId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90")),
                new Film("The Fast and the Curious"),
                new StartTime(LocalDateTime.parse("2025-03-23T14:30")),
                new Hall(SMALL_HALL),
                new RowNumber(row),
                new SeatNumber(seat));
    }

    @Test
    void toDto_singleTicket_mapsAllFields() {
        // arrange
        var ticket = ticket(4, 2);

        // act
        var dto = mapper.toDto(ticket);

        // assert
        assertThat(dto.film()).isEqualTo("The Fast and the Curious");
        assertThat(dto.start()).isEqualTo("2025-03-23T14:30");
        assertThat(dto.hall()).isEqualTo(SMALL_HALL);
        assertThat(dto.row()).isEqualTo(4);
        assertThat(dto.seat()).isEqualTo(2);
    }

    @Test
    void toDto_list_mapsEveryTicket() {
        // arrange
        var tickets = List.of(ticket(4, 1), ticket(4, 2));

        // act
        var dtos = mapper.toDto(tickets);

        // assert
        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(dto -> dto.seat()).containsExactly(1, 2);
    }
}
