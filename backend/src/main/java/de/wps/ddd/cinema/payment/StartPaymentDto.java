package de.wps.ddd.cinema.payment;

import de.wps.ddd.cinema.common.architecture.DomainCommand;

import java.util.UUID;

@DomainCommand
public record StartPaymentDto(UUID reference, long amountInCents) {
}
