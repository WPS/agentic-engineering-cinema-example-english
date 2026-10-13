package de.wps.ddd.cinema.filmselection.data;

import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface Screenings extends Repository<Screening, Long> {

    Screening save(Screening screening);

    Optional<Screening> findByUuid(UUID uuid);
}
