package ru.jerael.booktracker.backend.domain.exception.code;

public enum EmailVerificationErrorCode implements ErrorCode {
    VERIFICATION_NOT_FOUND,
    TOKEN_EXPIRED,
    INVALID_TOKEN
}
