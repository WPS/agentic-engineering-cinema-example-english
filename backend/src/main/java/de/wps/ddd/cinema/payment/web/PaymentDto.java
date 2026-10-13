package de.wps.ddd.cinema.payment.web;

record PaymentDto(String reference, long amountInCents, String status) {
}
