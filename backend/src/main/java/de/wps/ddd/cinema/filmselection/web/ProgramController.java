package de.wps.ddd.cinema.filmselection.web;

import de.wps.ddd.cinema.filmselection.data.Film;
import de.wps.ddd.cinema.filmselection.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/program")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @GetMapping()
    public List<Film> getFilmsForDay(@RequestParam LocalDate date) {
        return programService.getFilmsForDay(date);
    }
}
