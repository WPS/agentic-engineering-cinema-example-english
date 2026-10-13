package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.repositories;

import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.Hall;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.HallName;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.Halls;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository of the ticket sale's halls (like payment). It is filled by the
 * {@code HallsFixture}.
 */
@Component
public class HallRepository implements Halls {

    private final ConcurrentHashMap<HallName, Hall> halls = new ConcurrentHashMap<>();

    @Override
    public Optional<Hall> find(HallName name) {
        return Optional.ofNullable(halls.get(name));
    }

    @Override
    public void save(Hall hall) {
        halls.put(hall.name(), hall);
    }
}
