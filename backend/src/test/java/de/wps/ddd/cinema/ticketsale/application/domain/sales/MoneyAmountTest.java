package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyAmountTest {

    @Test
    void euroInCents_setsAmountInCents() {
        // act
        var amount = MoneyAmount.euroInCents(150);

        // assert
        assertThat(amount.getAmount()).isEqualTo(150);
        assertThat(amount.getCurrency()).isEqualTo(MoneyAmount.Currency.EUR);
    }

    @Test
    void euro_convertsEuroAndCentsToCents() {
        // act
        var amount = MoneyAmount.euro(10, 50);

        // assert
        assertThat(amount.getAmount()).isEqualTo(1050);
    }

    @Test
    void plus_addsAmounts() {
        // act
        var sum = MoneyAmount.euro(10, 50).plus(MoneyAmount.euro(2, 0));

        // assert
        assertThat(sum).isEqualTo(MoneyAmount.euro(12, 50));
    }

    @Test
    void times_multipliesAmount() {
        // act
        var amount = MoneyAmount.euro(10, 50).times(4);

        // assert
        assertThat(amount).isEqualTo(MoneyAmount.euro(42, 0));
    }

    @Test
    void times_zero_yieldsZero() {
        // act
        var amount = MoneyAmount.euro(10, 50).times(0);

        // assert
        assertThat(amount).isEqualTo(MoneyAmount.euroInCents(0));
    }

    @Test
    void euroInCents_negativeAmount_throwsException() {
        // act / assert
        assertThatThrownBy(() -> MoneyAmount.euroInCents(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be greater than or equal to 0");
    }

    @Test
    void euro_negativeAmount_throwsException() {
        // act / assert
        assertThatThrownBy(() -> MoneyAmount.euro(-1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be greater than or equal to 0");
    }

    @Test
    void times_negativeFactor_throwsException() {
        // act / assert
        assertThatThrownBy(() -> MoneyAmount.euro(10, 50).times(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Factor must be greater than or equal to 0");
    }

    @Test
    void equality_isBasedOnAmountAndCurrency() {
        // act / assert
        assertThat(MoneyAmount.euro(10, 50)).isEqualTo(MoneyAmount.euroInCents(1050));
        assertThat(MoneyAmount.euro(10, 50)).isNotEqualTo(MoneyAmount.euro(10, 51));
    }
}
