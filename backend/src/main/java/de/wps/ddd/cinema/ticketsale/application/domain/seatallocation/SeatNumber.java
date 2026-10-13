package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record SeatNumber(int value) implements Comparable<SeatNumber> {
    @Override
    public int compareTo(SeatNumber o) {
        return Integer.compare(this.value, o.value);
    }
}
