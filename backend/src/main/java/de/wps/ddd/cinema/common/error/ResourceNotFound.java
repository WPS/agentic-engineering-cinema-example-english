package de.wps.ddd.cinema.common.error;

public class ResourceNotFound extends RuntimeException {

    private ResourceNotFound(String message) {
        super(message);
    }

    public static void throwIf(boolean condition, String message) {
        if (condition) {
            throw new ResourceNotFound(message);
        }
    }
}
