package de.wps.ddd.cinema.ticketsale.adapters.primary.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** {@code @JsonProperty} keeps the JSON key {@code isFree} (instead of a derived {@code free}). */
public record SeatDto(int row, int number, @JsonProperty("isFree") boolean isFree) {
}
