package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.code.EmailVerificationErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import java.util.Map;

public class EmailVerificationErrorFactory {
    public static ValidationError tokenExpired() {
        return new ValidationError(
            EmailVerificationErrorCode.TOKEN_EXPIRED.name(),
            "Verification token has expired",
            "token",
            Map.of()
        );
    }

    public static ValidationError invalidToken() {
        return new ValidationError(
            EmailVerificationErrorCode.INVALID_TOKEN.name(),
            "Verification token is invalid",
            "token",
            Map.of()
        );
    }
}
