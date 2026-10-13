package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.ticketsale.application.domain.sales.TicketBlock;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.TicketsSold;
import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentReceived;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.CurrentScreenings;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.Tickets;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SalesNotifications;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SaleTransactions;
import lombok.RequiredArgsConstructor;
import org.jmolecules.event.annotation.DomainEventHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class ProcessPaymentReceipt implements de.wps.ddd.cinema.ticketsale.application.ports.primary.ProcessPaymentReceipt {

    private final SaleTransactions saleTransactions;

    private final CurrentScreenings currentScreenings;

    private final TicketBlock ticketBlock;

    private final SeatingPlans seatingPlans;

    private final Tickets tickets;

    private final SalesNotifications salesNotifications;

    @Override
    @DomainEventHandler
    public void execute(PaymentReceived paymentReceived) {
        var saleTransaction = saleTransactions.getByPaymentAttempt(paymentReceived.paymentAttemptId());
        saleTransaction.paymentReceived(paymentReceived.paymentAttemptId());

        var orderNumber = saleTransaction.getOrderNumber();
        var screeningId = saleTransaction.getScreeningId();
        var selectedSeats = saleTransaction.getSelectedSeats();

        var screening = currentScreenings.getScreening(screeningId);
        var issuedTickets = ticketBlock.createTickets(orderNumber, screening, selectedSeats);

        var seatingPlan = seatingPlans.getSeatingPlan(screeningId);
        seatingPlan.markAsSold(selectedSeats);
        seatingPlans.putBack(seatingPlan);

        saleTransaction.complete();

        tickets.save(issuedTickets);
        saleTransactions.save(saleTransaction);
        salesNotifications.publish(new TicketsSold(orderNumber, screening, issuedTickets));
    }
}
