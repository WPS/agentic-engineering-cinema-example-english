package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.repositories;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers.ScreeningEntityMapper;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.CurrentScreenings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrentScreeningsImpl implements CurrentScreenings {

    private final ScreeningRepository screeningRepository;

    private final ScreeningEntityMapper screeningMapper;

    @Override
    public List<Screening> allScreenings() {
        var screenings = screeningRepository.findAll();
        return screenings.stream().map(screeningMapper::toDomain).toList();
    }

    @Override
    public Screening getScreening(ScreeningId screeningId) {
        var screening = screeningRepository.findById(screeningId.value());
        ResourceNotFound.throwIf(screening.isEmpty(), "Screening " + screeningId + " does not exist");
        return screeningMapper.toDomain(screening.get());
    }

    @Override
    public Optional<Screening> find(Film film, Hall hall, StartTime start) {
        return screeningRepository.findByFilmAndHallAndStart(film.name(), hall.name().de(), hall.name().en(), start.value())
                .map(screeningMapper::toDomain);
    }

    @Override
    public void save(Screening screening) {
        screeningRepository.save(screeningMapper.toEntity(screening));
    }
}
