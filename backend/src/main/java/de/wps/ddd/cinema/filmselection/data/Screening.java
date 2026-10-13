package de.wps.ddd.cinema.filmselection.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jmolecules.ddd.annotation.Identity;

import java.time.LocalDateTime;
import java.util.UUID;

@org.jmolecules.ddd.annotation.Entity
@Entity
@Table(name = "screenings", schema = "film_selection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Screening {
    @Identity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @EqualsAndHashCode.Include
    private Long id;
    // The screening's identity shared with weekly planning and ticket sale; exposed as "id" in the API.
    @JsonProperty("id")
    private UUID uuid;
    @JsonIgnore
    @Column(name = "film_id")
    private Long filmId;
    private LocalDateTime start;
    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;
}
