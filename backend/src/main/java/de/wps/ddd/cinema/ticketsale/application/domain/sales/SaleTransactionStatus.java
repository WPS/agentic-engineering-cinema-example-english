package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import org.jmolecules.ddd.annotation.ValueObject;

/**
 * There is deliberately no cancelled state: a rejected PaymentAttempt may be retried.
 * Abandoned transactions therefore stay open — they would need an expiry date.
 */
@ValueObject
public enum SaleTransactionStatus {
    IN_PROGRESS,
    COMPLETED,
}
