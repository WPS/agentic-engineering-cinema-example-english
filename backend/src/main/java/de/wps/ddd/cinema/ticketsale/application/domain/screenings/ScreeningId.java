package de.wps.ddd.cinema.ticketsale.application.domain.screenings;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record ScreeningId(UUID value) {
}
