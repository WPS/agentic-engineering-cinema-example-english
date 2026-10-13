package de.wps.ddd.cinema.ticketsale.adapters.primary.web.model;

public record PaymentAttemptDto(
        String id,
        int attempt,
        MoneyAmountDto amount,
        String status
) {
}
