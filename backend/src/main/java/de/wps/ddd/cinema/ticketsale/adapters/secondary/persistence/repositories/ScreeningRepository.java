package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.repositories;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.ScreeningEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface ScreeningRepository extends JpaRepository<ScreeningEntity, UUID> {

    /** The hall is identified by both languages of its name. */
    @Query("""
            select s from ScreeningEntity s
            where s.film = :film and s.hall.de = :hallDe and s.hall.en = :hallEn and s.start = :start""")
    Optional<ScreeningEntity> findByFilmAndHallAndStart(@Param("film") String film,
                                                        @Param("hallDe") String hallDe,
                                                        @Param("hallEn") String hallEn,
                                                        @Param("start") LocalDateTime start);
}
