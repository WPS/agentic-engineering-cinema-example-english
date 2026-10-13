package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.repositories;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.SaleTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SaleTransactionRepository extends JpaRepository<SaleTransactionEntity, UUID> {

    @Query("select s from SaleTransactionEntity s where s.paymentAttempt.paymentAttemptId = :paymentAttemptId")
    Optional<SaleTransactionEntity> findByPaymentAttemptId(@Param("paymentAttemptId") UUID paymentAttemptId);
}
