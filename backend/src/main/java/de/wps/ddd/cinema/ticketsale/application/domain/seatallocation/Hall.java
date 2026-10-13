package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

/**
 * A hall of the ticket sale as an aggregate, identified by its {@link HallName}. Carries the seating
 * layout (rows × seats per row) from which an empty {@link SeatingPlan} is built.
 */
@AggregateRoot
public record Hall(@Identity HallName name, int rows, int seatsPerRow) {
}
