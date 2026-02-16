package ru.jerael.booktracker.backend.domain.exception;

public class ValidationException extends AppException {
    public ValidationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public static ValidationException invalidBookStatus(String status) {
        return new ValidationException(
            "Unknown book status: " + status,
            "INVALID_BOOK_STATUS"
        );
    }
}
