package de.wps.ddd.cinema.ticketsale.application.fixtures;

import de.wps.ddd.cinema.common.fixtures.Fixture;
import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.HallName;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.Halls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Provides the (demo) halls of the ticket sale. Thanks to {@link Order} it runs before the
 * {@code WeeklyPlanFixture}, so the halls already exist when the (async) adoption of the weekly plan
 * looks up their seating layout.
 */
@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class HallsFixture implements Fixture {

    public static final LocalizedText SMALL_HALL = LocalizedText.of("kleiner Saal", "Small Hall");
    public static final LocalizedText LARGE_HALL = LocalizedText.of("großer Saal", "Large Hall");

    private final Halls halls;

    @Override
    public void install() {
        log.info("Creating halls...");
        halls.save(new Hall(new HallName(SMALL_HALL), 4, 8));
        halls.save(new Hall(new HallName(LARGE_HALL), 6, 12));
    }
}
