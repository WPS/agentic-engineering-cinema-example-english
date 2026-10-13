package de.wps.ddd.cinema.weeklyplanning.filmcatalog;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDate;

/**
 * Planning-internal distribution data of a film (distributor, distribution fee, licence period). These
 * details stay inside weekly planning and never reach the exposed view DTOs.
 */
@ValueObject
public record DistributionDeal(String distributor, MoneyAmount distributionFee, LocalDate licenceStart, LocalDate licenceEnd) {
}
