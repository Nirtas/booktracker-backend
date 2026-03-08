package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.EmailVerificationEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaEmailVerificationRepository;
import ru.jerael.booktracker.backend.data.mapper.EmailVerificationDataMapper;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerification;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import ru.jerael.booktracker.backend.domain.repository.EmailVerificationRepository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EmailVerificationRepositoryImpl implements EmailVerificationRepository {
    private final JpaEmailVerificationRepository jpaEmailVerificationRepository;
    private final EmailVerificationDataMapper emailVerificationDataMapper;

    @Override
    public void deleteByUserIdAndType(UUID userId, VerificationType type) {
        jpaEmailVerificationRepository.deleteByUserIdAndType(userId, type);
    }

    @Override
    public Optional<EmailVerification> findByUserIdAndType(UUID userId, VerificationType type) {
        return jpaEmailVerificationRepository.findByUserIdAndType(userId, type)
            .map(emailVerificationDataMapper::toDomain);
    }

    @Override
    public EmailVerification save(EmailVerification emailVerification) {
        EmailVerificationEntity entity = emailVerificationDataMapper.toEntity(emailVerification);
        EmailVerificationEntity savedEntity = jpaEmailVerificationRepository.save(entity);
        return emailVerificationDataMapper.toDomain(savedEntity);
    }
}
