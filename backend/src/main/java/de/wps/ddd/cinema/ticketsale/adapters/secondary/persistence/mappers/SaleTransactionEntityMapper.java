package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.SaleTransactionEntity;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttempt;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransactionStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.RowNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatNumber;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SaleTransactionEntityMapper {

    public SaleTransactionEntity toEntity(SaleTransaction saleTransaction) {
        var seats = saleTransaction.getSelectedSeats().seats().stream()
                .map(seatId -> new SaleTransactionEntity.SeatEmbeddable(
                        seatId.row().value(), seatId.number().value()))
                .toList();
        return new SaleTransactionEntity(
                saleTransaction.getOrderNumber().value(),
                saleTransaction.getScreeningId().value(),
                new ArrayList<>(seats),
                saleTransaction.getTotalPrice().getAmount(),
                toEntity(saleTransaction.paymentAttempt().orElse(null)),
                saleTransaction.getAttempts(),
                saleTransaction.getStatus().name()
        );
    }

    public SaleTransaction toDomain(SaleTransactionEntity saleTransactionEntity) {
        var seats = saleTransactionEntity.getSeats().stream()
                .map(seat -> new SeatId(new RowNumber(seat.getRowNumber()), new SeatNumber(seat.getSeatNumber())))
                .toList();
        return new SaleTransaction(
                new OrderNumber(saleTransactionEntity.getOrderNumber()),
                new ScreeningId(saleTransactionEntity.getScreeningId()),
                new AdjacentSeats(seats),
                MoneyAmount.euroInCents(saleTransactionEntity.getTotalPriceInCents()),
                toDomain(saleTransactionEntity.getPaymentAttempt()),
                saleTransactionEntity.getAttempts(),
                SaleTransactionStatus.valueOf(saleTransactionEntity.getStatus())
        );
    }

    private SaleTransactionEntity.PaymentAttemptEmbeddable toEntity(PaymentAttempt paymentAttempt) {
        if (paymentAttempt == null) {
            return null;
        }
        return new SaleTransactionEntity.PaymentAttemptEmbeddable(
                paymentAttempt.getId().value(),
                paymentAttempt.getAttempt(),
                paymentAttempt.getAmount().getAmount(),
                paymentAttempt.getStatus().name());
    }

    private PaymentAttempt toDomain(SaleTransactionEntity.PaymentAttemptEmbeddable paymentAttempt) {
        if (paymentAttempt == null || paymentAttempt.getPaymentAttemptId() == null) {
            return null;
        }
        return new PaymentAttempt(
                new PaymentAttemptId(paymentAttempt.getPaymentAttemptId()),
                paymentAttempt.getAttempt(),
                MoneyAmount.euroInCents(paymentAttempt.getAmountInCents()),
                PaymentStatus.valueOf(paymentAttempt.getStatus()));
    }
}
