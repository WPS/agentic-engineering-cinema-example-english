package de.wps.ddd.cinema.ticketsale.application.domain.screenings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
@Getter
@AllArgsConstructor
public class Screening {
    @Identity
    private final ScreeningId id;
    private final Hall hall;
    private final StartTime start;
    private final Film film;
    private final ScreeningCategory category;
}
