package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.verification.EmailVerification;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationRepository {
    void deleteByUserIdAndType(UUID userId, VerificationType type);

    Optional<EmailVerification> findByUserIdAndType(UUID userId, VerificationType type);

    EmailVerification save(EmailVerification emailVerification);
}
