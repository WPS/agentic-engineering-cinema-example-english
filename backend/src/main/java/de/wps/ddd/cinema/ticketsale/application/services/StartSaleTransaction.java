package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.ticketsale.application.domain.sales.SaleTransaction;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SaleTransactions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class StartSaleTransaction implements de.wps.ddd.cinema.ticketsale.application.ports.primary.StartSaleTransaction {

    private final SaleTransactions saleTransactions;

    private final CalculateTotalPrice calculateTotalPrice;

    @Override
    public SaleTransaction execute(ScreeningId screeningId, AdjacentSeats selectedSeats) {
        var totalPrice = calculateTotalPrice.execute(screeningId, selectedSeats);

        var saleTransaction = SaleTransaction.start(screeningId, selectedSeats, totalPrice);
        saleTransactions.save(saleTransaction);
        // TODO
        // Block the seats in the SeatingPlan so they cannot be sold twice
        return saleTransaction;
    }
}
