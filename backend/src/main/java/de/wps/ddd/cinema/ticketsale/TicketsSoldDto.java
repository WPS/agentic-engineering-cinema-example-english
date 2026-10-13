package de.wps.ddd.cinema.ticketsale;

import de.wps.ddd.cinema.common.text.LocalizedText;
import org.jmolecules.event.annotation.DomainEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@DomainEvent
public record TicketsSoldDto(
        UUID orderNumber,
        UUID screeningId,
        String film,
        LocalDateTime start,
        LocalizedText hall,
        List<SoldTicket> tickets) {

    public record SoldTicket(UUID ticketId, int row, int seat) {
    }
}
