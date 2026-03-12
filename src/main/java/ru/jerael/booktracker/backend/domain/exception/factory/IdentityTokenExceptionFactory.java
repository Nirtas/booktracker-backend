package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException;
import ru.jerael.booktracker.backend.domain.exception.code.IdentityTokenErrorCode;

public class IdentityTokenExceptionFactory {
    public static UnauthenticatedException invalidSignature() {
        return new UnauthenticatedException(
            IdentityTokenErrorCode.INVALID_SIGNATURE,
            "Token signature is invalid"
        );
    }

    public static UnauthenticatedException tokenExpired() {
        return new UnauthenticatedException(
            IdentityTokenErrorCode.TOKEN_EXPIRED,
            "Token has expired"
        );
    }

    public static UnauthenticatedException tokenMalformed(String details) {
        return new UnauthenticatedException(
            IdentityTokenErrorCode.TOKEN_MALFORMED,
            "Token is invalid or corrupted: " + details
        );
    }

    public static UnauthenticatedException invalidIssuer(String issuer) {
        return new UnauthenticatedException(
            IdentityTokenErrorCode.INVALID_ISSUER,
            "Token issuer is invalid: " + issuer
        );
    }

    public static UnauthenticatedException invalidTokenType(String expected, String actual) {
        return new UnauthenticatedException(
            IdentityTokenErrorCode.INVALID_TOKEN_TYPE,
            "Expected token type " + expected + " but got " + actual
        );
    }
}
