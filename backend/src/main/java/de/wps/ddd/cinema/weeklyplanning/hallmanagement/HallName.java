package de.wps.ddd.cinema.weeklyplanning.hallmanagement;

import de.wps.ddd.cinema.common.text.LocalizedText;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record HallName(LocalizedText value) {
}
