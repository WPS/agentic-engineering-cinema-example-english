package de.wps.ddd.cinema.payment.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Amount(long cents) {
}
