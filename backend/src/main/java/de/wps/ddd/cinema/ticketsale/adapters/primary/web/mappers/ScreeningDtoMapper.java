package de.wps.ddd.cinema.ticketsale.adapters.primary.web.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.primary.web.model.ScreeningDto;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import org.springframework.stereotype.Component;

@Component
public class ScreeningDtoMapper {

    public ScreeningDto toDto(Screening screening) {
        return new ScreeningDto(
                screening.getId().value(),
                screening.getStart().value().toString(),
                screening.getHall().name(),
                screening.getFilm().name()
        );
    }

}
