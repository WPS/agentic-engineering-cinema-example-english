package de.wps.ddd.cinema.ticketsale.adapters.primary.web.controllers;

import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.PaymentDtoMapper;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.ScreeningDtoMapper;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.SeatingPlanDtoMapper;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers.TicketDtoMapper;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.AdjacentSeatsDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.MoneyAmountDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.PaymentAttemptDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.PaymentStatusDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.PriceRequestDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.SaleTransactionDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.ScreeningDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.SeatingPlanDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.TicketDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.TicketOrderDto;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCount;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.CalculateTotalPrice;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.FindAdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.GetPaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.GetSeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.GetSelectedScreening;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.GetTickets;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.StartPaymentAttempt;
import de.wps.ddd.cinema.ticketsale.application.ports.primary.StartSaleTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

/** The methods are ordered along the purchase flow. */
@Slf4j
@RestController
@RequestMapping(TicketSaleController.BASE_PATH)
@RequiredArgsConstructor
class TicketSaleController {

    static final String BASE_PATH = "/api/ticket-sale";

    private final ScreeningDtoMapper screeningMapper;
    private final SeatingPlanDtoMapper seatingPlanMapper;
    private final PaymentDtoMapper paymentMapper;
    private final TicketDtoMapper ticketMapper;

    private final GetSelectedScreening getSelectedScreening;
    private final GetSeatingPlan getSeatingPlan;
    private final FindAdjacentSeats findAdjacentSeats;
    private final CalculateTotalPrice calculateTotalPrice;
    private final StartSaleTransaction startSaleTransaction;
    private final StartPaymentAttempt startPaymentAttempt;
    private final GetPaymentStatus getPaymentStatus;
    private final GetTickets getTickets;

    @GetMapping("/screenings/{screeningId}")
    public ScreeningDto getSelectedScreening(@PathVariable UUID screeningId) {
        var screening = getSelectedScreening.execute(new ScreeningId(screeningId));
        return screeningMapper.toDto(screening);
    }

    @GetMapping("/seating-plans/{screeningId}")
    public SeatingPlanDto getSeatingPlan(@PathVariable UUID screeningId) {
        var seatingPlan = getSeatingPlan.execute(new ScreeningId(screeningId));
        return seatingPlanMapper.toDto(seatingPlan);
    }

    @GetMapping("/seating-plans/{screeningId}/adjacent-seats")
    public AdjacentSeatsDto findAdjacentSeats(@PathVariable UUID screeningId, @RequestParam int seatCount) {
        var seats = findAdjacentSeats.execute(new ScreeningId(screeningId), new SeatCount(seatCount));
        return seatingPlanMapper.toDto(seats);
    }

    @PostMapping("/price-requests")
    public MoneyAmountDto calculateTotalPrice(@RequestBody PriceRequestDto priceRequest) {
        var screeningId = new ScreeningId(priceRequest.screeningId());
        var selectedSeats = seatingPlanMapper.toDomain(priceRequest.seats());
        var totalPrice = calculateTotalPrice.execute(screeningId, selectedSeats);
        return paymentMapper.toDto(totalPrice);
    }

    @PostMapping("/sale-transactions")
    public ResponseEntity<SaleTransactionDto> startSaleTransaction(@RequestBody TicketOrderDto order) {
        var screeningId = new ScreeningId(order.screeningId());
        var selectedSeats = seatingPlanMapper.toDomain(order.seats());

        var saleTransaction = startSaleTransaction.execute(screeningId, selectedSeats);

        var dto = paymentMapper.toDto(saleTransaction);
        var location = UriComponentsBuilder.fromPath(BASE_PATH + "/sale-transactions/{orderNumber}")
                .buildAndExpand(dto.orderNumber()).toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @PostMapping("/sale-transactions/{orderNumber}/payment-attempts")
    public ResponseEntity<PaymentAttemptDto> startPaymentAttempt(@PathVariable UUID orderNumber) {
        var paymentAttempt = startPaymentAttempt.execute(new OrderNumber(orderNumber));

        var dto = paymentMapper.toDto(paymentAttempt);
        var location = UriComponentsBuilder
                .fromPath(BASE_PATH + "/sale-transactions/{orderNumber}/payment-attempts/{id}")
                .buildAndExpand(orderNumber, dto.id()).toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping("/sale-transactions/{orderNumber}/payment-status")
    public PaymentStatusDto getPaymentStatus(@PathVariable UUID orderNumber) {
        var status = getPaymentStatus.execute(new OrderNumber(orderNumber));
        return paymentMapper.toDto(status);
    }

    @GetMapping("/sale-transactions/{orderNumber}/tickets")
    public List<TicketDto> getTickets(@PathVariable UUID orderNumber) {
        var tickets = getTickets.execute(new OrderNumber(orderNumber));
        return ticketMapper.toDto(tickets);
    }
}
