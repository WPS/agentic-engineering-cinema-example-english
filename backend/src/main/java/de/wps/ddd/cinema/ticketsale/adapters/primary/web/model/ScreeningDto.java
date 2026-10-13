package de.wps.ddd.cinema.ticketsale.adapters.primary.web.model;

import de.wps.ddd.cinema.common.text.LocalizedText;

import java.util.UUID;

public record ScreeningDto(UUID id, String start, LocalizedText hall, String film) {
}
