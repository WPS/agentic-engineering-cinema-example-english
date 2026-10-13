package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.repositories;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.SeatingPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeatingPlanRepository extends JpaRepository<SeatingPlanEntity, Integer> {
    SeatingPlanEntity findByScreeningId(UUID screeningId);

    @Query("SELECT s.id FROM SeatingPlanEntity s WHERE s.screeningId = :screeningId")
    Optional<Integer> findIdByScreeningId(@Param("screeningId") UUID screeningId);
}
