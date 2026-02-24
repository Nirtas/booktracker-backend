package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;

public class ValidationException extends AppException {
    private final String field;

    public ValidationException(ErrorCode errorCode, String message, String field) {
        super(errorCode, message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
