package de.wps.ddd.cinema;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Pins the application clock in integration tests to the fixed reference day <b>2025-03-19</b> (Wed, CW 12/2025).
 * This way the {@code WeeklyPlanFixture} reproduces exactly the same schedule as at development time,
 * so the date literals of the integration tests ({@code of(2025, 3, ...)}) stay stable.
 *
 * @see IntegrationTestWithWeeklyPlan
 */
@TestConfiguration(proxyBeanMethods = false)
public class FixedClockTestConfig {

    private static final LocalDate REFERENCE_DAY = LocalDate.of(2025, 3, 19);

    @Bean
    @Primary
    Clock fixedClock() {
        return Clock.fixed(REFERENCE_DAY.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }
}
