package de.wps.ddd.cinema.common.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BusinessRuleViolatedTest {

    @Test
    void throwIf_conditionMet_throwsBusinessRuleViolated() {
        // arrange
        var message = "Only pending payment transactions can be completed.";

        // act / assert
        assertThatThrownBy(() -> BusinessRuleViolated.throwIf(true, message))
                .isInstanceOf(BusinessRuleViolated.class)
                .hasMessage(message);
    }

    @Test
    void throwIf_conditionNotMet_doesNotThrow() {
        // act / assert
        assertThatCode(() -> BusinessRuleViolated.throwIf(false, "must not be thrown"))
                .doesNotThrowAnyException();
    }
}
