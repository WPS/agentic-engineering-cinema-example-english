package de.wps.ddd.cinema.common.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceNotFoundTest {

    @Test
    void throwIf_conditionMet_throwsResourceNotFound() {
        // arrange
        var message = "Screening not found.";

        // act / assert
        assertThatThrownBy(() -> ResourceNotFound.throwIf(true, message))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage(message);
    }

    @Test
    void throwIf_conditionNotMet_doesNotThrow() {
        // act / assert
        assertThatCode(() -> ResourceNotFound.throwIf(false, "must not be thrown"))
                .doesNotThrowAnyException();
    }
}
