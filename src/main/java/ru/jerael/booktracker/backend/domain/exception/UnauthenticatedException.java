package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;

public class UnauthenticatedException extends AppException {
    public UnauthenticatedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
