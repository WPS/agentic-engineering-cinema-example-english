package de.wps.ddd.cinema.weeklyplanning.persistence;

import de.wps.ddd.cinema.weeklyplanning.filmcatalog.Film;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.FilmCatalog;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.FilmTitle;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FilmRepository implements FilmCatalog {

    private final ConcurrentHashMap<FilmTitle, Film> films = new ConcurrentHashMap<>();

    @Override
    public Optional<Film> find(FilmTitle title) {
        return Optional.ofNullable(films.get(title));
    }

    @Override
    public void save(Film film) {
        films.put(film.title(), film);
    }
}
