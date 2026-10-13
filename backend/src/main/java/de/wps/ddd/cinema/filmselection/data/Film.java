package de.wps.ddd.cinema.filmselection.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.wps.ddd.cinema.common.text.LocalizedText;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.List;

@AggregateRoot
@Entity
@Table(name = "films", schema = "film_selection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    @Identity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @EqualsAndHashCode.Include
    private Long id;
    private String title;
    private Integer runtimeMinutes;
    private String posterUrl;
    private Integer ageRating;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "de", column = @Column(name = "description_de", length = 4000)),
            @AttributeOverride(name = "en", column = @Column(name = "description_en", length = 4000))
    })
    private LocalizedText description;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "de", column = @Column(name = "genre_de")),
            @AttributeOverride(name = "en", column = @Column(name = "genre_en"))
    })
    private LocalizedText genre;
    private String leadActors;
    private String director;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "de", column = @Column(name = "language_de")),
            @AttributeOverride(name = "en", column = @Column(name = "language_en"))
    })
    private LocalizedText language;
    // Read-only convenience association for the program query (join fetch). The foreign key
    // film_id is written by the basic field filmId on Screening; therefore the association is
    // read-only here (otherwise the column film_id would be mapped twice).
    @OneToMany
    @JoinColumn(name = "film_id", insertable = false, updatable = false)
    private List<Screening> screenings;
}
