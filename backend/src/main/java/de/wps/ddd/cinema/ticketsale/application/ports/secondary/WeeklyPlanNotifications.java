package de.wps.ddd.cinema.ticketsale.application.ports.secondary;

import de.wps.ddd.cinema.common.architecture.Gateway;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.WeeklyPlanAdopted;

@Gateway
public interface WeeklyPlanNotifications {

    /** Publishes (module-internally) that the weekly plan has been adopted. */
    void publish(WeeklyPlanAdopted weeklyPlanAdopted);
}
