package de.wps.ddd.cinema.filmselection.service;

import de.wps.ddd.cinema.common.architecture.ApplicationService;
import de.wps.ddd.cinema.filmselection.data.CurrentFilms;
import de.wps.ddd.cinema.filmselection.data.Film;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ApplicationService
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final CurrentFilms currentFilms;

    public List<Film> getFilmsForDay(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = LocalTime.MAX.atDate(date);
        return currentFilms.findScreeningsBetween(start, end);
    }
}
