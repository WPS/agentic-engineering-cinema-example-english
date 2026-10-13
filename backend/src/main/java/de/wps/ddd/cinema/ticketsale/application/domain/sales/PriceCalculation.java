package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Seat;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import org.jmolecules.ddd.annotation.Service;
import org.springframework.stereotype.Component;

@Service
@Component
public class PriceCalculation {
    public MoneyAmount calculatePrice(
            Screening screening,
            SeatingPlan seatingPlan,
            AdjacentSeats adjacentSeats) {

        return adjacentSeats.seats().stream()
                .map(seatingPlan::seat)
                .map(seat -> calculatePrice(screening, seat))
                .reduce(MoneyAmount.euroInCents(0), MoneyAmount::plus);
    }

    private MoneyAmount calculatePrice(Screening screening, Seat seat) {
        return priceForScreening(screening)
                .plus(seatCategorySurcharge(seat));
    }

    private MoneyAmount priceForScreening(Screening screening) {
        return switch (screening.getCategory()) {
            case STANDARD -> MoneyAmount.euro(10, 50);
            case PRIME_TIME -> MoneyAmount.euro(12, 0);
            case CHILDREN -> MoneyAmount.euro(8, 50);
        };
    }

    private MoneyAmount seatCategorySurcharge(Seat seat) {
        return switch (seat.getCategory()) {
            case STALLS -> MoneyAmount.euro(0, 0);
            case BOX -> MoneyAmount.euro(2, 0);
        };
    }
}
