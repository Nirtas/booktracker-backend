package ru.jerael.booktracker.backend.data.exception.code;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;

public enum JwtProviderErrorCode implements ErrorCode {
    SIGNING_FAILED,
    INVALID_SIGNATURE,
    TOKEN_EXPIRED,
    TOKEN_MALFORMED,
    INVALID_ISSUER
}
