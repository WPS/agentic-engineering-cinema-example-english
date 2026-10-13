package de.wps.ddd.cinema.ticketsale.adapters.primary.web.model;

public record SaleTransactionDto(
        String orderNumber,
        String screeningId,
        AdjacentSeatsDto seats,
        MoneyAmountDto totalPrice
) {
}
