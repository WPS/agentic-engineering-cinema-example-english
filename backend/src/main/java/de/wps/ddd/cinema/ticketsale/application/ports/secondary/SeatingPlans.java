package de.wps.ddd.cinema.ticketsale.application.ports.secondary;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import org.jmolecules.ddd.annotation.Repository;

@Repository
public interface SeatingPlans {

    /** Takes the screening's seating plan from the stack; throws {@code ResourceNotFound} if it does not exist. */
    SeatingPlan getSeatingPlan(ScreeningId screeningId);

    /** Tells whether a seating plan exists for the screening. */
    boolean seatingPlanExists(ScreeningId screeningId);

    /** Puts the seating plan back on the stack (inserts or updates it). */
    void putBack(SeatingPlan seatingPlan);
}
