package de.wps.ddd.cinema.ticketsale.adapters.primary.web.controllers;

import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.common.web.GlobalExceptionHandler;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.PaymentDtoMapper;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.ScreeningDtoMapper;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.SeatingPlanDtoMapper;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.TicketDtoMapper;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransactionStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.Ticket;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.TicketId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningCategory;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Seat;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCategory;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCount;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.CalculateTotalPrice;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.FindAdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.GetPaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.GetSeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.GetSelectedScreening;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.GetTickets;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.StartPaymentAttempt;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.StartSaleTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TicketSaleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ScreeningDtoMapper.class, SeatingPlanDtoMapper.class, PaymentDtoMapper.class, TicketDtoMapper.class,
        GlobalExceptionHandler.class})
class TicketSaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String PRICE_REQUEST_JSON = """
            {"screeningId":"090c173a-3636-4980-865a-1ec859eb4f90",
             "seats":{"seats":[{"row":4,"number":1},{"row":4,"number":2}]}}""";

    @MockitoBean
    private GetSelectedScreening getSelectedScreening;
    @MockitoBean
    private GetSeatingPlan getSeatingPlan;
    @MockitoBean
    private FindAdjacentSeats findAdjacentSeats;
    @MockitoBean
    private CalculateTotalPrice calculateTotalPrice;
    @MockitoBean
    private StartSaleTransaction startSaleTransaction;
    @MockitoBean
    private StartPaymentAttempt startPaymentAttempt;
    @MockitoBean
    private GetPaymentStatus getPaymentStatus;
    @MockitoBean
    private GetTickets getTickets;

    private static final UUID SCREENING_ID = UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90");
    private static final UUID ORDER_NUMBER = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final LocalizedText SMALL_HALL = LocalizedText.of("kleiner Saal", "Small Hall");

    @Test
    void getSelectedScreening_returnsScreeningDto() throws Exception {
        // arrange
        when(getSelectedScreening.execute(new ScreeningId(SCREENING_ID))).thenReturn(screening());

        // act / assert
        mockMvc.perform(get("/api/ticket-sale/screenings/{screeningId}", SCREENING_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SCREENING_ID.toString()))
                .andExpect(jsonPath("$.start").value("2025-03-23T14:30"))
                .andExpect(jsonPath("$.film").value("The Fast and the Curious"))
                .andExpect(jsonPath("$.hall.de").value("kleiner Saal"))
                .andExpect(jsonPath("$.hall.en").value("Small Hall"));
    }

    @Test
    void getSeatingPlan_returnsSeatingPlanDtoWithIsFree() throws Exception {
        // arrange
        var seatingPlan = new SeatingPlan(new ScreeningId(SCREENING_ID), List.of(
                new Seat(seatId(1, 1), SeatCategory.STALLS, false),
                new Seat(seatId(1, 2), SeatCategory.STALLS, true)));
        when(getSeatingPlan.execute(new ScreeningId(SCREENING_ID))).thenReturn(seatingPlan);

        // act / assert
        mockMvc.perform(get("/api/ticket-sale/seating-plans/{screeningId}", SCREENING_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats[0][0].row").value(1))
                .andExpect(jsonPath("$.seats[0][0].number").value(1))
                .andExpect(jsonPath("$.seats[0][0].isFree").value(true))
                .andExpect(jsonPath("$.seats[0][1].isFree").value(false))
                .andExpect(jsonPath("$.seats[0][0].free").doesNotExist());
    }

    @Test
    void findAdjacentSeats_returnsFoundSeats() throws Exception {
        // arrange
        when(findAdjacentSeats.execute(new ScreeningId(SCREENING_ID), new SeatCount(2)))
                .thenReturn(new AdjacentSeats(List.of(seatId(4, 1), seatId(4, 2))));

        // act / assert
        mockMvc.perform(get("/api/ticket-sale/seating-plans/{screeningId}/adjacent-seats", SCREENING_ID)
                        .param("seatCount", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats.length()").value(2))
                .andExpect(jsonPath("$.seats[0].row").value(4))
                .andExpect(jsonPath("$.seats[0].number").value(1));
    }

    @Test
    void findAdjacentSeats_negativeSeatCount_returns400() throws Exception {
        // act / assert
        mockMvc.perform(get("/api/ticket-sale/seating-plans/{screeningId}/adjacent-seats", SCREENING_ID)
                        .param("seatCount", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateTotalPrice_returnsMoneyAmountDto() throws Exception {
        // arrange
        when(calculateTotalPrice.execute(eq(new ScreeningId(SCREENING_ID)), any())).thenReturn(MoneyAmount.euro(50, 0));

        // act / assert
        mockMvc.perform(post("/api/ticket-sale/price-requests")
                        .contentType(MediaType.APPLICATION_JSON).content(PRICE_REQUEST_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(5000))
                .andExpect(jsonPath("$.currency").value("EUR"));

        verify(calculateTotalPrice).execute(new ScreeningId(SCREENING_ID),
                new AdjacentSeats(List.of(seatId(4, 1), seatId(4, 2))));
    }

    @Test
    void startSaleTransaction_returnsTransactionWithOrderNumberAndTotalPrice() throws Exception {
        // arrange
        when(startSaleTransaction.execute(any(), any())).thenReturn(saleTransaction());
        var body = """
                {"screeningId":"%s","seats":{"seats":[{"row":4,"number":1}]}}""".formatted(SCREENING_ID);

        // act / assert
        mockMvc.perform(post("/api/ticket-sale/sale-transactions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/ticket-sale/sale-transactions/" + ORDER_NUMBER))
                .andExpect(jsonPath("$.orderNumber").value(ORDER_NUMBER.toString()))
                .andExpect(jsonPath("$.screeningId").value(SCREENING_ID.toString()))
                .andExpect(jsonPath("$.totalPrice.amount").value(5000))
                .andExpect(jsonPath("$.totalPrice.currency").value("EUR"))
                .andExpect(jsonPath("$.seats.seats[0].row").value(4))
                .andExpect(jsonPath("$.seats.seats[0].number").value(1));
    }

    @Test
    void startPaymentAttempt_returnsTheNewPaymentAttemptAndNeedsNoBody() throws Exception {
        // arrange
        var paymentAttempt = saleTransaction().startPaymentAttempt();
        when(startPaymentAttempt.execute(new OrderNumber(ORDER_NUMBER))).thenReturn(paymentAttempt);

        // act / assert
        mockMvc.perform(post("/api/ticket-sale/sale-transactions/{orderNumber}/payment-attempts", ORDER_NUMBER))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "/api/ticket-sale/sale-transactions/" + ORDER_NUMBER + "/payment-attempts/"
                                + paymentAttempt.getId().value()))
                .andExpect(jsonPath("$.id").value(paymentAttempt.getId().value().toString()))
                .andExpect(jsonPath("$.attempt").value(1))
                .andExpect(jsonPath("$.amount.amount").value(5000))
                .andExpect(jsonPath("$.amount.currency").value("EUR"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(startPaymentAttempt).execute(new OrderNumber(ORDER_NUMBER));
    }

    @Test
    void getPaymentStatus_returnsStatusDto() throws Exception {
        // arrange
        when(getPaymentStatus.execute(new OrderNumber(ORDER_NUMBER))).thenReturn(PaymentStatus.RECEIVED);

        // act / assert
        mockMvc.perform(get("/api/ticket-sale/sale-transactions/{orderNumber}/payment-status", ORDER_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void getPaymentStatus_cancelled_returnsCancelled() throws Exception {
        // arrange
        when(getPaymentStatus.execute(new OrderNumber(ORDER_NUMBER))).thenReturn(PaymentStatus.CANCELLED);

        // act / assert
        mockMvc.perform(get("/api/ticket-sale/sale-transactions/{orderNumber}/payment-status", ORDER_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void getTickets_returnsTicketDtos() throws Exception {
        // arrange
        when(getTickets.execute(new OrderNumber(ORDER_NUMBER))).thenReturn(List.of(ticket()));

        // act / assert
        mockMvc.perform(get("/api/ticket-sale/sale-transactions/{orderNumber}/tickets", ORDER_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].film").value("The Fast and the Curious"))
                .andExpect(jsonPath("$[0].start").value("2025-03-23T14:30"))
                .andExpect(jsonPath("$[0].hall.de").value("kleiner Saal"))
                .andExpect(jsonPath("$[0].hall.en").value("Small Hall"))
                .andExpect(jsonPath("$[0].row").value(4))
                .andExpect(jsonPath("$[0].seat").value(1));
    }

    private static Screening screening() {
        return new Screening(
                new ScreeningId(SCREENING_ID),
                new Hall(SMALL_HALL),
                new StartTime(LocalDateTime.parse("2025-03-23T14:30")),
                new Film("The Fast and the Curious"),
                ScreeningCategory.STANDARD);
    }

    private static SeatId seatId(int row, int number) {
        return new SeatId(new RowNumber(row), new SeatNumber(number));
    }

    private static SaleTransaction saleTransaction() {
        return new SaleTransaction(
                new OrderNumber(ORDER_NUMBER),
                new ScreeningId(SCREENING_ID),
                new AdjacentSeats(List.of(new SeatId(new RowNumber(4), new SeatNumber(1)))),
                MoneyAmount.euro(50, 0),
                null,
                0,
                SaleTransactionStatus.IN_PROGRESS);
    }

    private static Ticket ticket() {
        return new Ticket(
                TicketId.generate(),
                new OrderNumber(ORDER_NUMBER),
                new ScreeningId(SCREENING_ID),
                new Film("The Fast and the Curious"),
                new StartTime(LocalDateTime.parse("2025-03-23T14:30")),
                new Hall(SMALL_HALL),
                new RowNumber(4),
                new SeatNumber(1));
    }
}
