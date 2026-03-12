package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;

public class TooManyRequestsException extends AppException {
    public TooManyRequestsException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
