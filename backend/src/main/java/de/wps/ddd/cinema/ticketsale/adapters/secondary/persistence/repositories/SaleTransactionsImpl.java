package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.repositories;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers.SaleTransactionEntityMapper;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.OrderNumber;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SaleTransactions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleTransactionsImpl implements SaleTransactions {

    private final SaleTransactionRepository saleTransactionRepository;

    private final SaleTransactionEntityMapper saleTransactionMapper;

    @Override
    public void save(SaleTransaction saleTransaction) {
        var entity = saleTransactionMapper.toEntity(saleTransaction);
        saleTransactionRepository.save(entity);
    }

    @Override
    public SaleTransaction get(OrderNumber orderNumber) {
        var entity = saleTransactionRepository.findById(orderNumber.value());
        ResourceNotFound.throwIf(entity.isEmpty(), "Sale transaction " + orderNumber + " does not exist");
        return saleTransactionMapper.toDomain(entity.get());
    }

    @Override
    public SaleTransaction getByPaymentAttempt(PaymentAttemptId paymentAttemptId) {
        var entity = saleTransactionRepository.findByPaymentAttemptId(paymentAttemptId.value());
        ResourceNotFound.throwIf(entity.isEmpty(),
                "No sale transaction exists for payment attempt " + paymentAttemptId);
        return saleTransactionMapper.toDomain(entity.get());
    }
}
