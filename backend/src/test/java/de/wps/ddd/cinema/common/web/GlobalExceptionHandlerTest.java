package de.wps.ddd.cinema.common.web;

import de.wps.ddd.cinema.common.error.BusinessRuleViolated;
import de.wps.ddd.cinema.common.error.ResourceNotFound;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returnsProblemDetailWith404() {
        // arrange
        var ex = catchThrowableOfType(ResourceNotFound.class,
                () -> ResourceNotFound.throwIf(true, "Screening does not exist"));

        // act
        var problem = handler.handleNotFound(ex);

        // assert
        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Resource not found");
        assertThat(problem.getDetail()).isEqualTo("Screening does not exist");
    }

    @Test
    void handleBusinessRuleViolated_returnsProblemDetailWith409() {
        // arrange
        var ex = catchThrowableOfType(BusinessRuleViolated.class,
                () -> BusinessRuleViolated.throwIf(true, "Payment has not been received yet"));

        // act
        var problem = handler.handleBusinessRuleViolated(ex);

        // assert
        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("Business rule violated");
        assertThat(problem.getDetail()).isEqualTo("Payment has not been received yet");
    }

    @Test
    void handleInvalidRequest_returnsProblemDetailWith400() {
        // arrange
        var ex = new IllegalArgumentException("Seat count must not be negative.");

        // act
        var problem = handler.handleInvalidRequest(ex);

        // assert
        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Invalid request");
        assertThat(problem.getDetail()).isEqualTo("Seat count must not be negative.");
    }
}
