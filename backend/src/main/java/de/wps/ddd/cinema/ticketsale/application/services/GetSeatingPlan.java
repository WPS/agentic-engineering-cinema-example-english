package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetSeatingPlan implements de.wps.ddd.cinema.ticketsale.application.ports.primary.GetSeatingPlan {

    private final SeatingPlans seatingPlans;

    @Override
    public SeatingPlan execute(ScreeningId screeningId) {
        return seatingPlans.getSeatingPlan(screeningId);
    }
}
