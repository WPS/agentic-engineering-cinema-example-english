package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.ScreeningEntity;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningCategory;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import org.springframework.stereotype.Component;

@Component
public class ScreeningEntityMapper {

    public Screening toDomain(ScreeningEntity entity) {
        return new Screening(
                new ScreeningId(entity.getId()),
                new Hall(entity.getHall()),
                new StartTime(entity.getStart()),
                new Film(entity.getFilm()),
                ScreeningCategory.valueOf(entity.getCategory())
        );
    }

    public ScreeningEntity toEntity(Screening screening) {
        return new ScreeningEntity(
                screening.getId().value(),
                screening.getStart().value(),
                screening.getHall().name(),
                screening.getFilm().name(),
                screening.getCategory().name()
        );
    }
}
