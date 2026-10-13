package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.ticketsale.application.domain.sales.MoneyAmount;
import de.wps.ddd.cinema.ticketsale.application.domain.sales.PriceCalculation;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.CurrentScreenings;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CalculateTotalPrice implements de.wps.ddd.cinema.ticketsale.application.ports.primary.CalculateTotalPrice {

    private final CurrentScreenings currentScreenings;

    private final SeatingPlans seatingPlans;

    private final PriceCalculation priceCalculation;

    @Override
    public MoneyAmount execute(ScreeningId screeningId, AdjacentSeats adjacentSeats) {
        var screening = currentScreenings.getScreening(screeningId);
        var seatingPlan = seatingPlans.getSeatingPlan(screeningId);
        return priceCalculation.calculatePrice(screening, seatingPlan, adjacentSeats);
    }
}
