package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.repositories;

import de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {

    List<TicketEntity> findByOrderNumber(UUID orderNumber);
}
