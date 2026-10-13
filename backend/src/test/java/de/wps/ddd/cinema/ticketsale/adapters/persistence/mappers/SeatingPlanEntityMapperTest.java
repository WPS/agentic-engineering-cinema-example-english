package de.wps.ddd.cinema.ticketsale.adapters.persistence.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers.SeatingPlanEntityMapper;
import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.SeatingPlanEntity;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Seat;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCategory;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SeatingPlanEntityMapperTest {

    private final SeatingPlanEntityMapper mapper = new SeatingPlanEntityMapper();

    private final int seatingPlanId = 2;
    private final SeatNumber seatNumber = new SeatNumber(1);
    private final RowNumber rowNumber = new RowNumber(42);
    private final ScreeningId screeningId = new ScreeningId(UUID.fromString("a095c8f6-6fa2-4f2e-acf1-52cee0698e74"));

    @ParameterizedTest
    @CsvSource({
            "BOX,    false",
            "BOX,    true",
            "STALLS, false",
            "STALLS, true",
    })
    void toEntity(SeatCategory category, boolean sold) {
        // arrange
        var seats = List.of(new Seat(new SeatId(rowNumber, seatNumber), category, sold));
        var seatingPlan = new SeatingPlan(screeningId, seats);

        // act
        var seatingPlanEntity = mapper.toEntity(seatingPlan, seatingPlanId);

        // assert
        assertThat(seatingPlanEntity.getId()).isEqualTo(seatingPlanId);
        assertThat(seatingPlanEntity.getScreeningId()).isEqualTo(screeningId.value());
        assertThat(seatingPlanEntity.getSeats()).hasSize(1);

        var seatEntity = seatingPlanEntity.getSeats().getFirst();
        assertThat(seatEntity.getId().getSeatingPlanId()).isEqualTo(seatingPlanId);
        assertThat(seatEntity.getId().getRowNumber()).isEqualTo(rowNumber.value());
        assertThat(seatEntity.getId().getSeatNumber()).isEqualTo(seatNumber.value());
        assertThat(seatEntity.getCategory()).isEqualTo(category.name());
        assertThat(seatEntity.isSold()).isEqualTo(sold);
    }

    @ParameterizedTest
    @CsvSource({
            "BOX,    false",
            "BOX,    true",
            "STALLS, false",
            "STALLS, true",
    })
    void toDomain(SeatCategory category, boolean sold) {
        // arrange
        var seatingPlanEntity = new SeatingPlanEntity(seatingPlanId, screeningId.value());
        seatingPlanEntity.addSeat(rowNumber.value(), seatNumber.value(), category.name(), sold);

        // act
        var seatingPlan = mapper.toDomain(seatingPlanEntity);

        // assert
        assertThat(seatingPlan.getScreeningId()).isEqualTo(screeningId);
        var seat = seatingPlan.seat(new SeatId(rowNumber, seatNumber));
        assertThat(seat.getId().row()).isEqualTo(rowNumber);
        assertThat(seat.getId().number()).isEqualTo(seatNumber);
        assertThat(seat.getCategory()).isEqualTo(category);
        assertThat(seat.isSold()).isEqualTo(sold);
    }

}
