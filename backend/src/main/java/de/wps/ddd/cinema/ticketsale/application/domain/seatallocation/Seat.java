package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity
@Getter
@AllArgsConstructor
public class Seat {

    @Identity
    private final SeatId id;
    private final SeatCategory category;
    private boolean sold;

    public void markAsSold() {
        sold = true;
    }

    public boolean isFree() {
        return !sold;
    }

    public boolean isSold() {
        return sold;
    }

}
