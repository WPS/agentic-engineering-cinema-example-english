package de.wps.ddd.cinema.ticketsale.adapters.web.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.SeatingPlanDtoMapper;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.AdjacentSeatsDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.SeatDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.SeatIdDto;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Seat;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCategory;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SeatingPlanDtoMapperTest {

    private final SeatingPlanDtoMapper mapper = new SeatingPlanDtoMapper();

    private final RowNumber rowNumber = new RowNumber(3);
    private final SeatNumber seatNumber = new SeatNumber(2);
    private final SeatId seatId = new SeatId(rowNumber, seatNumber);

    @Test
    void seatingPlanToSeatingPlanDto() {
        // arrange

        var rowNumber1 = new RowNumber(1);
        var rowNumber2 = new RowNumber(2);

        var seatNumber1 = new SeatNumber(1);
        var seatNumber2 = new SeatNumber(2);

        var seatId1 = new SeatId(rowNumber1, seatNumber1);
        var seatId2 = new SeatId(rowNumber1, seatNumber2);
        var seatId3 = new SeatId(rowNumber2, seatNumber1);
        var seatId4 = new SeatId(rowNumber2, seatNumber2);

        var category = SeatCategory.BOX;

        var row1Seat1 = new Seat(seatId1, category, false);
        var row1Seat2 = new Seat(seatId2, category, true);
        var row2Seat1 = new Seat(seatId3, category, true);
        var row2Seat2 = new Seat(seatId4, category, false);

        var seats = List.of(row1Seat1, row1Seat2, row2Seat1, row2Seat2);

        var row1Seat1Dto = new SeatDto(1, 1, true);
        var row1Seat2Dto = new SeatDto(1, 2, false);
        var row2Seat1Dto = new SeatDto(2, 1, false);
        var row2Seat2Dto = new SeatDto(2, 2, true);

        var screeningId = new ScreeningId(UUID.randomUUID());
        var seatingPlan = new SeatingPlan(screeningId, seats);
        SeatDto[][] expectedSeatDtos = {
                {row1Seat1Dto, row1Seat2Dto},
                {row2Seat1Dto, row2Seat2Dto},
        };

        // act
        var seatingPlanDto = mapper.toDto(seatingPlan);

        // assert
        assertThat(seatingPlanDto.seats()).isEqualTo(expectedSeatDtos);
    }

    @Test
    void adjacentSeatsToDto_withSeat() {
        // arrange
        var adjacentSeats = new AdjacentSeats(List.of(seatId));

        // act
        var dto = mapper.toDto(adjacentSeats);

        // assert
        assertThat(dto.seats()).hasSize(1);
        var seatDto = dto.seats().getFirst();
        assertThat(seatDto.row()).isEqualTo(rowNumber.value());
        assertThat(seatDto.number()).isEqualTo(seatNumber.value());
    }

    @Test
    void adjacentSeatsToDto_empty() {
        // arrange
        var adjacentSeats = new AdjacentSeats(List.of());

        // act
        var dto = mapper.toDto(adjacentSeats);

        // assert
        assertThat(dto.seats()).isEmpty();
    }

    @Test
    void adjacentSeatsToDomain_withSeat() {
        // arrange
        var adjacentSeatsDto =
                new AdjacentSeatsDto(List.of(new SeatIdDto(rowNumber.value(), seatNumber.value())));

        // act
        var seats = mapper.toDomain(adjacentSeatsDto);

        // assert
        assertThat(seats.seats()).hasSize(1);
        var seat = seats.seats().getFirst();
        assertThat(seat.row()).isEqualTo(rowNumber);
        assertThat(seat.number()).isEqualTo(seatNumber);
    }

    @Test
    void adjacentSeatsToDomain_empty() {
        // arrange
        var adjacentSeatsDto = new AdjacentSeatsDto(List.of());

        // act
        var seats = mapper.toDomain(adjacentSeatsDto);

        // assert
        assertThat(seats.seats()).isEmpty();
    }
}
