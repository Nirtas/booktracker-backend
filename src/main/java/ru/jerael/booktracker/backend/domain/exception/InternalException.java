package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;

public class InternalException extends AppException {
    private final Throwable cause;

    public InternalException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
