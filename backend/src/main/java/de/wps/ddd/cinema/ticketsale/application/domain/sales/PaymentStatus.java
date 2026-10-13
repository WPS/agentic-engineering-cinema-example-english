package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum PaymentStatus {
    PENDING,
    RECEIVED,
    CANCELLED,
}
