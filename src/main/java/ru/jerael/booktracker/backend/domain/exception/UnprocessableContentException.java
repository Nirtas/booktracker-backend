package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;

public class UnprocessableContentException extends AppException {
    private final String field;

    public String getField() {
        return field;
    }

    public UnprocessableContentException(ErrorCode errorCode, String message, String field) {
        super(errorCode, message);
        this.field = field;
    }
}
