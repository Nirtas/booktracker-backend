package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.code.EmailVerificationErrorCode;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import java.util.UUID;

public class EmailVerificationExceptionFactory {
    public static NotFoundException verificationNotFound(UUID userId, VerificationType type) {
        return new NotFoundException(
            EmailVerificationErrorCode.VERIFICATION_NOT_FOUND,
            String.format("Verification for user '%s' with request '%s' was not found", userId, type)
        );
    }
}
