package de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.MoneyAmountDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.PaymentAttemptDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.PaymentStatusDto;
import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.SaleTransactionDto;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttempt;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentStatus;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDtoMapper {

    private final SeatingPlanDtoMapper seatingPlanMapper;

    public SaleTransactionDto toDto(SaleTransaction saleTransaction) {
        return new SaleTransactionDto(
                saleTransaction.getOrderNumber().value().toString(),
                saleTransaction.getScreeningId().value().toString(),
                seatingPlanMapper.toDto(saleTransaction.getSelectedSeats()),
                toDto(saleTransaction.getTotalPrice()));
    }

    public PaymentAttemptDto toDto(PaymentAttempt paymentAttempt) {
        return new PaymentAttemptDto(
                paymentAttempt.getId().value().toString(),
                paymentAttempt.getAttempt(),
                toDto(paymentAttempt.getAmount()),
                paymentAttempt.getStatus().name());
    }

    public MoneyAmountDto toDto(MoneyAmount moneyAmount) {
        return new MoneyAmountDto(moneyAmount.getAmount(), moneyAmount.getCurrency().name());
    }

    public PaymentStatusDto toDto(PaymentStatus status) {
        return new PaymentStatusDto(status.name());
    }
}
