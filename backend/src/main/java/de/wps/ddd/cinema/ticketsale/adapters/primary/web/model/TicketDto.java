package de.wps.ddd.cinema.ticketsale.adapters.primary.web.model;

import de.wps.ddd.cinema.common.text.LocalizedText;

public record TicketDto(
        String film,
        String start,
        LocalizedText hall,
        int row,
        int seat
) {
}
