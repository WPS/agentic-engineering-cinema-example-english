package de.wps.ddd.cinema.filmselection.data;

import de.wps.ddd.cinema.common.text.LocalizedText;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface Halls extends Repository<Hall, Long> {

    Optional<Hall> findByName(LocalizedText name);

    Hall save(Hall hall);
}
