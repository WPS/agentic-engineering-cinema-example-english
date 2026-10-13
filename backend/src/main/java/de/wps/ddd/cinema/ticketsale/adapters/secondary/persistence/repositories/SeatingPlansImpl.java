package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.repositories;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.mappers.SeatingPlanEntityMapper;
import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.SeatingPlanEntity;
import de.wps.ddd.cinema.ticketsale.application.domain.screenings.ScreeningId;
import de.wps.ddd.cinema.ticketsale.application.domain.seatallocation.SeatingPlan;
import de.wps.ddd.cinema.ticketsale.application.ports.secondary.SeatingPlans;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SeatingPlansImpl implements SeatingPlans {

    private final SeatingPlanRepository seatingPlanRepository;
    private final SeatingPlanEntityMapper seatingPlanMapper;

    @Override
    public SeatingPlan getSeatingPlan(ScreeningId screeningId) {
        SeatingPlanEntity seatingPlanEntity = seatingPlanRepository.findByScreeningId(screeningId.value());
        ResourceNotFound.throwIf(seatingPlanEntity == null,
                "Seating plan for screening " + screeningId + " does not exist");
        return seatingPlanMapper.toDomain(seatingPlanEntity);
    }

    @Override
    public boolean seatingPlanExists(ScreeningId screeningId) {
        return seatingPlanRepository.findIdByScreeningId(screeningId.value()).isPresent();
    }

    @Override
    public void putBack(SeatingPlan seatingPlan) {
        Integer seatingPlanEntityId = seatingPlanRepository
                .findIdByScreeningId(seatingPlan.getScreeningId().value()).orElse(null);
        SeatingPlanEntity seatingPlanEntity = seatingPlanMapper.toEntity(seatingPlan, seatingPlanEntityId);
        seatingPlanRepository.save(seatingPlanEntity);
    }
}
