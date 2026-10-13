package de.wps.ddd.cinema.filmselection.data;

import com.fasterxml.jackson.annotation.JsonValue;
import de.wps.ddd.cinema.common.text.LocalizedText;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jmolecules.ddd.annotation.Identity;

@org.jmolecules.ddd.annotation.Entity
@Entity
@Table(name = "halls", schema = "film_selection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Hall {
    @Identity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    // A hall serializes as its bilingual name: {"de": "…", "en": "…"}.
    @JsonValue
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "de", column = @Column(name = "name_de")),
            @AttributeOverride(name = "en", column = @Column(name = "name_en"))
    })
    private LocalizedText name;
}
