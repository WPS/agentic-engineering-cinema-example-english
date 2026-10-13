package de.wps.ddd.cinema.ticketsale.application.domain.screenings;

import org.jmolecules.event.annotation.DomainEvent;

/**
 * Ticket-sale-internal event: the weekly plan has been imported (screenings persisted, empty seating
 * plans created). Triggers the {@code SeatingPlanOccupancyFixture}, which then adds the demo occupancy.
 */
@DomainEvent
public record WeeklyPlanAdopted(int year, int calendarWeek) {
}
