package de.wps.ddd.cinema.ticketsale.application.ports.secondary;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.HallName;
import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;

/**
 * Repository port for the halls of the ticket sale (master data: seating layout per hall). Weekly
 * planning only provides the hall name; the seating layout belongs to the ticket sale. The demo halls are
 * provided by a fixture.
 */
@Repository
public interface Halls {

    /** Finds the hall with the given name. */
    Optional<Hall> find(HallName name);

    /** Saves (inserts or replaces) the hall. */
    void save(Hall hall);
}
