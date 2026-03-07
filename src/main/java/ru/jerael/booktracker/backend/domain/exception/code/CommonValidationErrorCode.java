package ru.jerael.booktracker.backend.domain.exception.code;

public enum CommonValidationErrorCode implements ErrorCode {
    FIELD_CANNOT_BE_EMPTY,
    FIELD_TOO_LONG,
    FIELD_TOO_SHORT
}
