package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.IntegrationTestWithWeeklyPlan;
import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import de.wps.ddd.cinema.ticketsale.TicketsSoldDto;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.payment.PaymentReceived;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.GetTickets;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.StartSaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.StartPaymentAttempt;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.ProcessPaymentReceipt;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SaleTransactions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.test.AssertablePublishedEvents;
import org.springframework.modulith.test.PublishedEventsExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The {@link PublishedEventsExtension} resolves the {@link AssertablePublishedEvents} parameter —
 * with a plain {@code @SpringBootTest} it is (unlike with {@code @ApplicationModuleTest}) not
 * registered automatically.
 */
@SpringBootTest
@Transactional
@ExtendWith(PublishedEventsExtension.class)
class ProcessPaymentReceiptIntegrationTest extends IntegrationTestWithWeeklyPlan {

    @Autowired
    private StartSaleTransaction startSaleTransaction;

    @Autowired
    private StartPaymentAttempt startPaymentAttempt;

    @Autowired
    private ProcessPaymentReceipt processPaymentReceipt;

    @Autowired
    private SaleTransactions saleTransactions;

    @Autowired
    private GetTickets getTickets;

    private final AdjacentSeats selectedSeats = new AdjacentSeats(List.of(
            seatId(4, 1), seatId(4, 2), seatId(4, 3), seatId(4, 4)));

    @Test
    void paymentReceived_completesTheSaleAndIssuesTheOrderedTickets(
            AssertablePublishedEvents publishedEvents) {
        // arrange
        var screeningId = screeningId("The Fast and the Curious", SMALL_HALL, LocalDateTime.of(2025, 3, 23, 14, 30));
        var orderNumber = startSaleTransaction.execute(screeningId, selectedSeats).getOrderNumber();
        var paymentAttemptId = startPaymentAttempt.execute(orderNumber).getId();

        // act
        processPaymentReceipt.execute(new PaymentReceived(paymentAttemptId));

        // assert
        var tickets = getTickets.execute(orderNumber);
        assertThat(tickets).hasSize(4);
        assertThat(tickets).allSatisfy(ticket -> {
            assertThat(ticket.getOrderNumber()).isEqualTo(orderNumber);
            assertThat(ticket.getScreeningId()).isEqualTo(screeningId);
            assertThat(ticket.getFilm().name()).isEqualTo("The Fast and the Curious");
            assertThat(ticket.getHall().name()).isEqualTo(SMALL_HALL);
            assertThat(ticket.getRow().value()).isEqualTo(4);
        });
        assertThat(tickets).extracting(ticket -> ticket.getSeat().value())
                .containsExactlyInAnyOrder(1, 2, 3, 4);

        publishedEvents.assertThat().contains(TicketsSoldDto.class)
                .matching(event -> event.orderNumber().equals(orderNumber.value()))
                .matching(event -> event.screeningId().equals(screeningId.value()))
                .matching(event -> event.film().equals("The Fast and the Curious"))
                .matching(event -> event.hall().equals(SMALL_HALL))
                .matching(event -> event.tickets().size() == 4);
    }

    @Test
    void paymentReceived_twice_issuesNoSecondTickets() {
        // arrange
        var screeningId = screeningId("The Fast and the Curious", SMALL_HALL, LocalDateTime.of(2025, 3, 23, 14, 30));
        var orderNumber = startSaleTransaction.execute(screeningId, selectedSeats).getOrderNumber();
        var paymentAttemptId = startPaymentAttempt.execute(orderNumber).getId();
        processPaymentReceipt.execute(new PaymentReceived(paymentAttemptId));

        // act / assert
        assertThatThrownBy(() -> processPaymentReceipt.execute(new PaymentReceived(paymentAttemptId)))
                .isInstanceOf(BusinessRuleViolated.class);
        assertThat(getTickets.execute(orderNumber)).hasSize(4);
        assertThat(saleTransactions.get(orderNumber).isInProgress()).isFalse();
    }

    private static SeatId seatId(int row, int number) {
        return new SeatId(new RowNumber(row), new SeatNumber(number));
    }
}
