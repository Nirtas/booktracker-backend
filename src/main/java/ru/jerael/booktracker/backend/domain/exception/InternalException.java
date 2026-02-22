package ru.jerael.booktracker.backend.domain.exception;

public class InternalException extends AppException {
    private final Throwable cause;

    public InternalException(String errorCode, String message, Throwable cause) {
        super(errorCode, message);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }

    public static InternalException storageError(String details, Throwable cause) {
        return new InternalException(
            "STORAGE_ERROR",
            "Error in storage: " + details,
            cause
        );
    }
}
