package de.wps.ddd.cinema.common.text;

import jakarta.persistence.Embeddable;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

/**
 * A text the cinema shows to visitors in German and English (film descriptions, genres, hall names, …).
 * Serialized to JSON as {@code {"de": "…", "en": "…"}}; the frontend picks the visitor's language.
 * Embeddable, so entities store it as two columns (override the column names per attribute).
 */
@ValueObject
@Embeddable
public record LocalizedText(String de, String en) {

    public LocalizedText {
        Objects.requireNonNull(de, "de");
        Objects.requireNonNull(en, "en");
    }

    public static LocalizedText of(String de, String en) {
        return new LocalizedText(de, en);
    }
}
