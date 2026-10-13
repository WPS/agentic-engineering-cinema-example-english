package de.wps.ddd.cinema.weeklyplanning.weeklyplan;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;

/**
 * Planning-internal times of a screening (admission, start, end of film). To the outside, the weekly
 * plan view exposes only the {@link #start()}.
 */
@ValueObject
public record ScreeningTimes(LocalDateTime admission, LocalDateTime start, LocalDateTime filmEnd) {

    private static final int ADMISSION_LEAD_MINUTES = 20;
    private static final int ADVERTISING_MINUTES = 15;

    public static ScreeningTimes schedule(LocalDateTime start, int runtimeMinutes) {
        var admission = start.minusMinutes(ADMISSION_LEAD_MINUTES);
        var filmEnd = start.plusMinutes((long) ADVERTISING_MINUTES + runtimeMinutes);
        return new ScreeningTimes(admission, start, filmEnd);
    }
}
