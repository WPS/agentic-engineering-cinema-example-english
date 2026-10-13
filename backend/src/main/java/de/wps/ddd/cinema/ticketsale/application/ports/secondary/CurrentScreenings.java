package de.wps.ddd.cinema.ticketsale.application.ports.secondary;

import de.wps.ddd.cinema.ticketsale.application.domain.screenings.StartTime;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Film;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.Screening;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrentScreenings {
    /** Returns all screenings known to the ticket sale. */
    List<Screening> allScreenings();

    /** Returns the screening with the given id; throws {@code ResourceNotFound} if it does not exist. */
    Screening getScreening(ScreeningId screeningId);

    /** Finds the screening of the given film in the given hall at the given start time. */
    Optional<Screening> find(Film film, Hall hall, StartTime start);

    /** Saves (inserts or updates) the screening. */
    void save(Screening screening);
}
