package de.wps.ddd.cinema.ticketsale;

import de.wps.ddd.cinema.IntegrationTestWithWeeklyPlan;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.TicketBlock;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.Ticket;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PriceCalculation;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCount;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.CurrentScreenings;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TicketSaleIntegrationTest extends IntegrationTestWithWeeklyPlan {

    @Autowired
    private CurrentScreenings screenings;

    @Autowired
    private SeatingPlans seatingPlans;

    @Autowired
    private PriceCalculation priceCalculation;

    @Autowired
    private TicketBlock ticketBlock;

    // Based on domain story 1: selling tickets at the box office
    @Test
    void ticketSale() {

        // 1. Visitor picks a screening from the weekly plan → screening selected
        var screeningId = screeningId("The Fast and the Curious", SMALL_HALL, LocalDateTime.of(2025, 3, 23, 14, 30));
        var screening = screenings.getScreening(screeningId);
        assertThat(screening.getFilm()).isEqualTo(new Film("The Fast and the Curious"));
        assertThat(screening.getHall()).isEqualTo(new Hall(SMALL_HALL));
        assertThat(screening.getStart()).isEqualTo(new StartTime(LocalDateTime.parse("2025-03-23T14:30")));

        // 2. Visitor asks for tickets for the screening → seat/ticket count requested
        var seatCount = new SeatCount(4);

        // 3. Cashier takes the screening's seating plan from the stack of seating plans → seating plan taken
        var seatingPlan = seatingPlans.getSeatingPlan(screeningId);
        assertThat(seatingPlan.getScreeningId()).isEqualTo(screeningId);

        // 4. Cashier looks for the requested number of seats in the seating plan → adjacent seats found
        // 5a. Cashier offers the seats found → seats offered
        var proposedSeats = seatingPlan.findAdjacentSeats(seatCount);
        assertThat(proposedSeats.count()).isEqualTo(seatCount);
        assertThat(proposedSeats.seats()).allMatch(seatId -> seatingPlan.seat(seatId).isFree());
        assertThat(proposedSeats.seats()).extracting(seatId -> seatId.row().value()).containsExactly(4, 4, 4, 4);
        assertThat(proposedSeats.seats()).extracting(seatId -> seatId.number().value()).containsExactly(1, 2, 3, 4);

        // 5b. Visitor accepts the seats → seats selected
        var selectedSeats = new AdjacentSeats(proposedSeats.seats().stream().toList());
        assertThat(selectedSeats.count()).isEqualTo(seatCount);

        // 6. Visitor pays the amount → payment made
        var totalAmount = priceCalculation.calculatePrice(screening, seatingPlan, selectedSeats);
        assertThat(totalAmount).isEqualTo(MoneyAmount.euro(50, 0));

        var orderNumber = payAmount(screeningId, selectedSeats, totalAmount);

        // 7. Cashier marks the sold seats in the seating plan → seats marked as sold
        seatingPlan.markAsSold(selectedSeats);
        assertThat(selectedSeats.seats()).allMatch(seatId -> seatingPlan.seat(seatId).isSold());

        // 8. Cashier puts the seating plan back on the stack → seating plan put back
        seatingPlans.putBack(seatingPlan);

        // 9. Cashier fills in the tickets → tickets issued
        var tickets = ticketBlock.createTickets(orderNumber, screening, selectedSeats);
        assertThat(tickets).hasSize(seatCount.value());
        assertThat(tickets).allSatisfy(ticket -> {
            assertThat(ticket.getFilm()).isEqualTo(screening.getFilm());
            assertThat(ticket.getStart()).isEqualTo(screening.getStart());
            assertThat(ticket.getHall()).isEqualTo(screening.getHall());
            assertThat(ticket.getRow().value()).isEqualTo(4);
        });
        assertThat(tickets).extracting(t -> t.getSeat().value()).containsExactly(1, 2, 3, 4);

        // 10. Cashier hands over the finished tickets → tickets handed over/sold
        assertThat(tickets).allSatisfy(ticket -> {
            assertThat(ticket.getOrderNumber()).isEqualTo(orderNumber);
            assertThat(ticket.getScreeningId()).isEqualTo(screeningId);
        });
        assertThat(tickets).extracting(Ticket::getId).doesNotHaveDuplicates();
    }

    private static @NonNull OrderNumber payAmount(ScreeningId screeningId, AdjacentSeats selectedSeats, MoneyAmount amount) {
        var saleTransaction = SaleTransaction.start(screeningId, selectedSeats, amount);
        var paymentAttempt = saleTransaction.startPaymentAttempt();
        assertThat(paymentAttempt.getAmount()).isEqualTo(amount);
        assertThat(paymentAttempt.getStatus()).isEqualTo(PaymentStatus.PENDING);

        saleTransaction.paymentReceived(paymentAttempt.getId());
        assertThat(paymentAttempt.getStatus()).isEqualTo(PaymentStatus.RECEIVED);
        return saleTransaction.getOrderNumber();
    }

}
