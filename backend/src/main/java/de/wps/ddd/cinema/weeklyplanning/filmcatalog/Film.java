package de.wps.ddd.cinema.weeklyplanning.filmcatalog;

import de.wps.ddd.cinema.common.text.LocalizedText;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

/**
 * A film as an aggregate of the film catalog, identified by its {@link FilmTitle}. Besides the master
 * data it holds planning-internal details ({@link DistributionDeal}) that no other context needs.
 * Texts shown to visitors (description, genre, language) are bilingual.
 */
@AggregateRoot
public record Film(
        @Identity FilmTitle title,
        int runtimeMinutes,
        String posterUrl,
        LocalizedText description,
        LocalizedText genre,
        String leadActors,
        String director,
        LocalizedText language,
        AgeRating ageRating,
        DistributionDeal distributionDeal) {
}
