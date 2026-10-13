package de.wps.ddd.cinema.ticketsale.application.services;

import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.CurrentScreenings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetSelectedScreening implements de.wps.ddd.cinema.ticketsale.application.ports.primary.GetSelectedScreening {

    private final CurrentScreenings currentScreenings;

    @Override
    public Screening execute(ScreeningId screeningId) {
        return currentScreenings.getScreening(screeningId);
    }
}
