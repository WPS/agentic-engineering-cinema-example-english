package de.wps.ddd.cinema.ticketsale.application.domain.seatallocation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RowNumberTest {

    @Test
    void compareTo_comparesNumerically() {
        // act / assert
        assertThat(new RowNumber(1)).isLessThan(new RowNumber(2));
        assertThat(new RowNumber(5)).isGreaterThan(new RowNumber(2));
        assertThat(new RowNumber(3)).isEqualByComparingTo(new RowNumber(3));
    }
}
