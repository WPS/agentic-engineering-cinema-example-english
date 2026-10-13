package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.List;

@ValueObject
public record AdjacentSeats(List<SeatId> seats) {
    public SeatCount count() {
        return new SeatCount(seats.size());
    }
}
