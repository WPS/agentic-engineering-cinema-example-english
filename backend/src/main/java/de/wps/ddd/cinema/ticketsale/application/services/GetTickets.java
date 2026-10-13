package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.Ticket;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.Tickets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetTickets implements de.wps.ddd.cinema.ticketsale.application.ports.primary.GetTickets {

    private final Tickets tickets;

    @Override
    public List<Ticket> execute(OrderNumber orderNumber) {
        var soldTickets = tickets.find(orderNumber);
        ResourceNotFound.throwIf(soldTickets.isEmpty(),
                "Order " + orderNumber + " has no sold tickets");
        return soldTickets;
    }
}
