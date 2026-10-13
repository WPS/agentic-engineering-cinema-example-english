package de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.AdjacentSeatsDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.SeatDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.SeatIdDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.SeatingPlanDto;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Seat;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SeatingPlanDtoMapper {

    public SeatingPlanDto toDto(SeatingPlan seatingPlan) {
        var seats = seatingPlan.getSeats();

        var rowCount = seats.size();
        var firstRow = seats.values().stream().findFirst();
        var seatsPerRow = firstRow.map(Map::size).orElse(0);
        var seatDtos = new SeatDto[rowCount][seatsPerRow];

        seats.forEach((rowNumber, row) -> row.forEach((seatNumber, seat) ->
                seatDtos[rowNumber.value() - 1][seatNumber.value() - 1] = toDto(seat)
        ));

        return new SeatingPlanDto(seatDtos);
    }

    public AdjacentSeatsDto toDto(AdjacentSeats adjacentSeats) {
        return new AdjacentSeatsDto(adjacentSeats.seats().stream().map(this::toDto).toList());
    }

    public AdjacentSeats toDomain(AdjacentSeatsDto adjacentSeats) {
        return new AdjacentSeats(adjacentSeats.seats().stream().map(this::toDomain).toList());
    }

    private SeatDto toDto(Seat seat) {
        return new SeatDto(
                seat.getId().row().value(),
                seat.getId().number().value(),
                seat.isFree()
        );
    }

    private SeatIdDto toDto(SeatId seatId) {
        return new SeatIdDto(seatId.row().value(), seatId.number().value());
    }

    private SeatId toDomain(SeatIdDto seatId) {
        return new SeatId(new RowNumber(seatId.row()), new SeatNumber(seatId.number()));
    }
}
