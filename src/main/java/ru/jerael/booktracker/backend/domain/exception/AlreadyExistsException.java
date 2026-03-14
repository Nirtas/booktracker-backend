package ru.jerael.booktracker.backend.domain.exception;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;

public class AlreadyExistsException extends AppException {
    public AlreadyExistsException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
