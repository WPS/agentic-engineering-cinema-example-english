package de.wps.ddd.cinema.ticketsale.application.domain.payment;

import de.wps.ddd.cinema.ticketsale.application.domain.sales.PaymentAttemptId;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent
public record PaymentCancelled(PaymentAttemptId paymentAttemptId) {
}
