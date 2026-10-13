package de.wps.ddd.cinema.payment;

import org.jmolecules.event.annotation.DomainEvent;

import java.util.UUID;

@DomainEvent
public record PaymentReceivedDto(UUID reference) {
}
