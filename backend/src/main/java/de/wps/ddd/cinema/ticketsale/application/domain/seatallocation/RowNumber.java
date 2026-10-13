package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record RowNumber(int value) implements Comparable<RowNumber> {
    @Override
    public int compareTo(RowNumber o) {
        return Integer.compare(this.value, o.value);
    }
}
