package de.wps.ddd.cinema.ticketsale.application.domain.screenings;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;

@ValueObject
public record StartTime(LocalDateTime value) {
}
