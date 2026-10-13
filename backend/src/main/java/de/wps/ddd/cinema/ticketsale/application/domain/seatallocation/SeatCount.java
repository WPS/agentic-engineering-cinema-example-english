package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.util.Assert;

@ValueObject
public record SeatCount(int value) {
    public SeatCount {
        Assert.isTrue(value >= 0, "Seat count must not be negative.");
    }
}
