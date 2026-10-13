package de.wps.ddd.cinema.weeklyplanning.filmcatalog;

import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;

@Repository
public interface FilmCatalog {

    Optional<Film> find(FilmTitle title);

    void save(Film film);
}
