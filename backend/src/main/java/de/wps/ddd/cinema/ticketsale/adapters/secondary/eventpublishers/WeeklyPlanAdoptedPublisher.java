package de.wps.ddd.cinema.ticketsale.adapters.secondary.eventpublishers;

import de.wps.ddd.cinema.ticketsale.application.domain.screenings.WeeklyPlanAdopted;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.WeeklyPlanNotifications;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeeklyPlanAdoptedPublisher implements WeeklyPlanNotifications {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(WeeklyPlanAdopted weeklyPlanAdopted) {
        eventPublisher.publishEvent(weeklyPlanAdopted);
    }
}
