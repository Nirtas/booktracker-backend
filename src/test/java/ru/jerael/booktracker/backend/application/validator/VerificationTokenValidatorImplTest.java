package ru.jerael.booktracker.backend.application.validator;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerification;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class VerificationTokenValidatorImplTest {
    private final VerificationTokenValidatorImpl validator = new VerificationTokenValidatorImpl();

    private final UUID id = UUID.fromString("d756300f-6a7f-45df-96c7-55977999f7c9");
    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final VerificationType type = VerificationType.REGISTRATION;
    private final String token = "token";
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    private EmailVerification createVerification(Instant expiresAt) {
        return new EmailVerification(id, userId, "test@example.com", type, token, expiresAt, createdAt);
    }

    @Test
    void validate_ShouldNotThrowException_WhenTokenIsValid() {
        EmailVerification verification = createVerification(Instant.now().plusSeconds(60L));

        assertDoesNotThrow(() -> validator.validate(verification, token));
    }

    @Test
    void validate_ShouldThrowValidationException_WhenTokenIsExpired() {
        EmailVerification verification = createVerification(Instant.now().minusSeconds(60L));

        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validate(verification, token));

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.message().contains("expired")));
    }

    @Test
    void validate_ShouldThrowValidationException_WhenTokenMismatched() {
        EmailVerification verification = createVerification(Instant.now().plusSeconds(60L));

        ValidationException ex =
            assertThrows(ValidationException.class, () -> validator.validate(verification, "invalid token"));

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.message().contains("invalid")));
    }

    @Test
    void validate_ShouldAccumulateMultipleErrors_WhenTokenIsExpiredAndMismatched() {
        EmailVerification verification = createVerification(Instant.now().minusSeconds(60L));

        ValidationException ex =
            assertThrows(ValidationException.class, () -> validator.validate(verification, "invalid token"));

        assertEquals(2, ex.getErrors().size());
    }
}