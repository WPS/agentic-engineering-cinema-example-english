package de.wps.ddd.cinema.common.error;

public class BusinessRuleViolated extends RuntimeException {

    private BusinessRuleViolated(String message) {
        super(message);
    }

    public static void throwIf(boolean condition, String message) {
        if (condition) {
            throw new BusinessRuleViolated(message);
        }
    }
}
