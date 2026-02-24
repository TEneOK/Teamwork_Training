package org.skypro.teamwork.exception;

public class DatabaseAccessException extends RecommendationServiceException {
    public DatabaseAccessException(String message) {
        super(message);
    }

    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}