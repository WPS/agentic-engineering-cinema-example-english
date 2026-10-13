package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model;

import de.wps.ddd.cinema.common.text.LocalizedText;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "screenings", schema = "ticket_sale")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ScreeningEntity {
    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private LocalDateTime start;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "de", column = @Column(name = "hall_de")),
            @AttributeOverride(name = "en", column = @Column(name = "hall_en"))
    })
    private LocalizedText hall;
    private String film;
    private String category;
}
