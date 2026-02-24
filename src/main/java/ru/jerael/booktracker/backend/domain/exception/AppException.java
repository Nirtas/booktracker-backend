package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;

public abstract class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
