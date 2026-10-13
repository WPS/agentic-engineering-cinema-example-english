package de.wps.ddd.cinema.ticketsale.adapters.primary.eventlisteners;

import de.wps.ddd.cinema.ticketsale.application.ports.primary.ImportWeeklyPlan;
import de.wps.ddd.cinema.weeklyplanning.WeeklyPlanCreatedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service("ticketSaleWeeklyPlanEventListener")
@RequiredArgsConstructor
public class WeeklyPlanEventListener {

    private final ImportWeeklyPlan importWeeklyPlan;

    @ApplicationModuleListener
    public void on(WeeklyPlanCreatedDto dto) {
        importWeeklyPlan.execute(dto.year(), dto.calendarWeek());
    }
}
