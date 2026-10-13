package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.SeatingPlanEntity;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Seat;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCategory;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class SeatingPlanEntityMapper {

    public SeatingPlanEntity toEntity(SeatingPlan seatingPlan, @Nullable Integer seatingPlanId) {
        var seatingPlanEntity = new SeatingPlanEntity(seatingPlanId, seatingPlan.getScreeningId().value());
        seatingPlan.getSeats().values().stream()
                .flatMap(row -> row.values().stream())
                .forEach(seat -> seatingPlanEntity.addSeat(
                        seat.getId().row().value(),
                        seat.getId().number().value(),
                        seat.getCategory().name(),
                        seat.isSold()
                ));
        return seatingPlanEntity;
    }

    public SeatingPlan toDomain(SeatingPlanEntity seatingPlanEntity) {
        return new SeatingPlan(
                new ScreeningId(seatingPlanEntity.getScreeningId()),
                seatingPlanEntity.getSeats().stream().map(
                        seat -> new Seat(
                                new SeatId(
                                        new RowNumber(seat.getId().getRowNumber()),
                                        new SeatNumber(seat.getId().getSeatNumber())),
                                SeatCategory.valueOf(seat.getCategory()),
                                seat.isSold()
                        )
                ).toList());
    }
}
