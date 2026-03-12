package ru.jerael.booktracker.backend.data.exception.factory;

import ru.jerael.booktracker.backend.data.exception.code.JwtProviderErrorCode;
import ru.jerael.booktracker.backend.domain.exception.InternalException;

public class JwtProviderExceptionFactory {
    public static InternalException signingFailed(String details, Throwable cause) {
        return new InternalException(
            JwtProviderErrorCode.SIGNING_FAILED,
            "Failed to sign token: " + details,
            cause
        );
    }
}
