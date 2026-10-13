package de.wps.ddd.cinema.ticketsale.adapters.primary.web.model;

import java.util.UUID;

public record PriceRequestDto(UUID screeningId, AdjacentSeatsDto seats) {
}
