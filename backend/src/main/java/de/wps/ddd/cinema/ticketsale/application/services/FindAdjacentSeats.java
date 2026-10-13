package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatCount;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.AdjacentSeats;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindAdjacentSeats implements de.wps.ddd.cinema.ticketsale.application.ports.primary.FindAdjacentSeats {

    private final SeatingPlans seatingPlans;

    @Override
    public AdjacentSeats execute(ScreeningId screeningId, SeatCount seatCount) {
        log.info("Looking for {} adjacent seats for screening {}", seatCount.value(), screeningId);
        var seatingPlan = seatingPlans.getSeatingPlan(screeningId);
        var result = seatingPlan.findAdjacentSeats(seatCount);
        log.info("Adjacent seats found for screening {}: {}", screeningId, result.seats());
        return result;
    }
}
