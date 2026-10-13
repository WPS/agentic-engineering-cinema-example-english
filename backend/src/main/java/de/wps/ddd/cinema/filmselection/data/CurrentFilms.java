package de.wps.ddd.cinema.filmselection.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.jmolecules.ddd.annotation.Repository
@org.springframework.stereotype.Repository
public interface CurrentFilms extends Repository<Film, Long> {
    @Query("select f from Film f join fetch f.screenings s where s.start >= :start and s.start < :end")
    List<Film> findScreeningsBetween(LocalDateTime start, LocalDateTime end);

    Optional<Film> findByTitle(String title);

    Film save(Film film);
}
