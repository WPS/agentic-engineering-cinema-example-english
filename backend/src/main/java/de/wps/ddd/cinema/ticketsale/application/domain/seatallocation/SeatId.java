package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record SeatId(RowNumber row, SeatNumber number) {
}
