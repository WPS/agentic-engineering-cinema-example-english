package de.wps.ddd.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.Clock;

@SpringBootApplication
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * The application's time source. In production the system clock, so the demo data (see
     * {@code WeeklyPlanFixture}) always reflects the current calendar week. Tests override
     * this bean with a fixed clock (reference day 2025-03-19) to get deterministic dates.
     */
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
