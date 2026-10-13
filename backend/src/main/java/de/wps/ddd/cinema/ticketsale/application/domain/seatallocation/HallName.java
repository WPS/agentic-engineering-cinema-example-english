package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import de.wps.ddd.cinema.common.text.LocalizedText;
import org.jmolecules.ddd.annotation.ValueObject;

/** Identity of a {@link Hall}: its bilingual name (both languages take part in equality). */
@ValueObject
public record HallName(LocalizedText value) {
}
