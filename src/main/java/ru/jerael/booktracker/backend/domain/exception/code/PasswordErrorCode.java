package ru.jerael.booktracker.backend.domain.exception.code;

public enum PasswordErrorCode implements ErrorCode {
    NEEDS_LOWERCASE,
    NEEDS_UPPERCASE,
    NEEDS_DIGIT,
    NEEDS_SPECIAL_CHAR,
    FORBIDDEN_CHAR
}
