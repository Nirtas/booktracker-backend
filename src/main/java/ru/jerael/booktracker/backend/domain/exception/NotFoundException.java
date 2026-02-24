package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;

public class NotFoundException extends AppException {
    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
