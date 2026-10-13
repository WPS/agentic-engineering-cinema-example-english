package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.repositories;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers.TicketEntityMapper;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.Ticket;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.Tickets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketsImpl implements Tickets {

    private final TicketRepository ticketRepository;

    private final TicketEntityMapper ticketMapper;

    @Override
    public void save(List<Ticket> tickets) {
        var entities = tickets.stream().map(ticketMapper::toEntity).toList();
        ticketRepository.saveAll(entities);
    }

    @Override
    public List<Ticket> find(OrderNumber orderNumber) {
        return ticketRepository.findByOrderNumber(orderNumber.value()).stream()
                .map(ticketMapper::toDomain)
                .toList();
    }
}
