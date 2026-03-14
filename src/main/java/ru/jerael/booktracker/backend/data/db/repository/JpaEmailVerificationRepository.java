package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.EmailVerificationEntity;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaEmailVerificationRepository extends JpaRepository<EmailVerificationEntity, UUID> {
    void deleteByUserIdAndType(UUID userId, VerificationType type);

    Optional<EmailVerificationEntity> findByUserIdAndType(UUID userId, VerificationType type);
}
