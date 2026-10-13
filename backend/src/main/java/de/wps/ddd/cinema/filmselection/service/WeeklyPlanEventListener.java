package de.wps.ddd.cinema.filmselection.service;

import de.wps.ddd.cinema.weeklyplanning.WeeklyPlanCreatedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service("filmSelectionWeeklyPlanEventListener")
@RequiredArgsConstructor
public class WeeklyPlanEventListener {

    private final WeeklyPlanImportService weeklyPlanImportService;

    @ApplicationModuleListener
    public void on(WeeklyPlanCreatedDto dto) {
        weeklyPlanImportService.importWeeklyPlan(dto.year(), dto.calendarWeek());
    }
}
