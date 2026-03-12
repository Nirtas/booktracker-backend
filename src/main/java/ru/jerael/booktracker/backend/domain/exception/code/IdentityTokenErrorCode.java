package ru.jerael.booktracker.backend.domain.exception.code;

public enum IdentityTokenErrorCode implements ErrorCode {
    INVALID_SIGNATURE,
    TOKEN_EXPIRED,
    TOKEN_MALFORMED,
    INVALID_ISSUER,
    INVALID_TOKEN_TYPE,
    INVALID_TOKEN
}