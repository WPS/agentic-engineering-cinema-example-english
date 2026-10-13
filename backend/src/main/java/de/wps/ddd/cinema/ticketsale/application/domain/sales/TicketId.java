package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record TicketId(UUID value) {
    public static TicketId generate() {
        return new TicketId(UUID.randomUUID());
    }
}
