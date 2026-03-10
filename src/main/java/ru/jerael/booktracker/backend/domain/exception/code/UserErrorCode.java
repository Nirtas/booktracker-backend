package ru.jerael.booktracker.backend.domain.exception.code;

public enum UserErrorCode implements ErrorCode {
    EMAIL_ALREADY_EXISTS,
    USER_NOT_FOUND,
    ALREADY_VERIFIED,
    INVALID_CREDENTIALS,
    USER_NOT_VERIFIED
}
