package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record OrderNumber(UUID value) {
    public static OrderNumber generate() {
        return new OrderNumber(UUID.randomUUID());
    }
}
