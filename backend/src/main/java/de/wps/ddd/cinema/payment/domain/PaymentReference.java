package de.wps.ddd.cinema.payment.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record PaymentReference(UUID value) {
}
