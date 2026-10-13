package de.wps.ddd.cinema.ticketsale.application.domain.screenings;

import de.wps.ddd.cinema.common.text.LocalizedText;
import org.jmolecules.ddd.annotation.ValueObject;

/** The hall a screening takes place in, as visitors see it: its bilingual name. */
@ValueObject
public record Hall(LocalizedText name) {
}
