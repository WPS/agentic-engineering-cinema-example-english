package de.wps.ddd.cinema.payment.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum PaymentStatus {
    OPEN,
    RECEIVED,
    CANCELLED,
}
