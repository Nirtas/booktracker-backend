package ru.jerael.booktracker.backend.data.exception.factory;

import ru.jerael.booktracker.backend.data.exception.code.JwtProviderErrorCode;
import ru.jerael.booktracker.backend.domain.exception.InternalException;
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;

public class JwtProviderExceptionFactory {
    public static InternalException signingFailed(String details, Throwable cause) {
        return new InternalException(
            JwtProviderErrorCode.SIGNING_FAILED,
            "Failed to sign token: " + details,
            cause
        );
    }

    public static UnauthenticatedException invalidSignature() {
        return new UnauthenticatedException(
            JwtProviderErrorCode.INVALID_SIGNATURE,
            "Token signature is invalid"
        );
    }

    public static UnauthenticatedException tokenExpired() {
        return new UnauthenticatedException(
            JwtProviderErrorCode.TOKEN_EXPIRED,
            "Token has expired"
        );
    }

    public static UnauthenticatedException tokenMalformed(String details) {
        return new UnauthenticatedException(
            JwtProviderErrorCode.TOKEN_MALFORMED,
            "Token is invalid or corrupted: " + details
        );
    }

    public static UnauthenticatedException invalidIssuer(String issuer) {
        return new UnauthenticatedException(
            JwtProviderErrorCode.INVALID_ISSUER,
            "Token issuer is invalid: " + issuer
        );
    }

    public static UnauthenticatedException invalidTokenType(String expected, String actual) {
        return new UnauthenticatedException(
            JwtProviderErrorCode.INVALID_TOKEN_TYPE,
            "Expected token type " + expected + " but got " + actual
        );
    }
}
