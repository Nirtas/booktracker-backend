package ru.jerael.booktracker.backend.domain.exception;

public class InternalException extends AppException {
    private final Throwable cause;

    public InternalException(String message, String errorCode, Throwable cause) {
        super(message, errorCode);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }

    public static InternalException storageError(String details, Throwable cause) {
        return new InternalException(
            "Error in storage: " + details,
            "STORAGE_ERROR",
            cause
        );
    }
}
