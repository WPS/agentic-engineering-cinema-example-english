package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Association;
import org.jmolecules.ddd.annotation.Identity;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AggregateRoot
public class SeatingPlan {

    @Identity
    @Association(aggregateType = Screening.class)
    private final ScreeningId screeningId;
    private final TreeMap<RowNumber, TreeMap<SeatNumber, Seat>> seats;

    public SeatingPlan(ScreeningId screeningId, List<Seat> seats) {
        Assert.notNull(screeningId, "screeningId must not be null");
        this.screeningId = screeningId;
        this.seats = seats.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getId().row(),
                        TreeMap::new, // outer map - sorted by row number
                        Collectors.toMap(
                                s -> s.getId().number(),
                                Function.identity(),
                                (s1, s2) -> s1, // handle duplicate keys if needed
                                TreeMap::new    // inner map - sorted by seat number
                        )
                ));
    }

    /**
     * Creates an empty SeatingPlan for a hall's seating layout: all seats free, the front half of the
     * rows stalls, the back half box.
     */
    public static SeatingPlan empty(ScreeningId screeningId, int rows, int seatsPerRow) {
        var seats = new ArrayList<Seat>(rows * seatsPerRow);
        for (int row = 1; row <= rows; row++) {
            var category = row <= rows / 2 ? SeatCategory.STALLS : SeatCategory.BOX;
            for (int number = 1; number <= seatsPerRow; number++) {
                seats.add(new Seat(new SeatId(new RowNumber(row), new SeatNumber(number)), category, false));
            }
        }
        return new SeatingPlan(screeningId, seats);
    }

    /**
     * @param seatCount the number of requested free adjacent seats
     * @return the first free adjacent seats starting from the back row, or an empty list if there are no
     * seatCount adjacent seats
     */
    public AdjacentSeats findAdjacentSeats(SeatCount seatCount) {
        BusinessRuleViolated.throwIf(seatCount.value() < 1, "At least one seat must be requested.");
        var result = new ArrayList<SeatId>();
        for (var row : seats.descendingMap().values()) {
            for (Seat seat : row.values()) {
                if (seat.isFree()) {
                    result.add(seat.getId());
                    if (result.size() == seatCount.value()) {
                        return new AdjacentSeats(result);
                    }
                } else {
                    result.clear();
                }
            }
        }
        return new AdjacentSeats(result);
    }

    public void markAsSold(AdjacentSeats adjacentSeats) {
        for (SeatId seatId : adjacentSeats.seats()) {
            seat(seatId).markAsSold();
        }
    }

    public Seat seat(SeatId id) {
        return this.seats.get(id.row()).get(id.number());
    }

}
