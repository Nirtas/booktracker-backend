package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.repository.JpaEmailVerificationRepository;
import ru.jerael.booktracker.backend.data.mapper.EmailVerificationDataMapper;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerification;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({EmailVerificationRepositoryImpl.class, EmailVerificationDataMapper.class})
class EmailVerificationRepositoryImplTest {

    @Autowired
    private EmailVerificationRepositoryImpl emailVerificationRepository;

    @Autowired
    private JpaEmailVerificationRepository jpaEmailVerificationRepository;

    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String email = "test@example.com";
    private final VerificationType type = VerificationType.REGISTRATION;
    private final String token = "token";
    private final Instant expiresAt = Instant.ofEpochMilli(1771249999347L);
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    @Test
    void deleteByUserIdAndType_ShouldDeleteEmailVerificationByUserIdAndType() {
        EmailVerification emailVerification =
            new EmailVerification(null, userId, email, type, token, expiresAt, createdAt);

        EmailVerification createdEmailVerification = emailVerificationRepository.save(emailVerification);
        assertNotNull(createdEmailVerification.id());

        emailVerificationRepository.deleteByUserIdAndType(userId, type);
        assertEquals(Optional.empty(), emailVerificationRepository.findByUserIdAndType(userId, type));
    }

    @Test
    void findByUserIdAndType_WhenExists_ShouldReturnEmailVerification() {
        EmailVerification emailVerification =
            new EmailVerification(null, userId, email, type, token, expiresAt, createdAt);

        EmailVerification createdEmailVerification = emailVerificationRepository.save(emailVerification);
        assertNotNull(createdEmailVerification.id());

        Optional<EmailVerification> result = emailVerificationRepository.findByUserIdAndType(userId, type);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().userId());
        assertEquals(email, result.get().email());
        assertEquals(type, result.get().type());
    }

    @Test
    void save_WhenIdIsNull_ShouldInsertNewEmailVerification() {
        EmailVerification emailVerification =
            new EmailVerification(null, userId, email, type, token, expiresAt, createdAt);

        EmailVerification createdEmailVerification = emailVerificationRepository.save(emailVerification);

        assertNotNull(createdEmailVerification.id());
        assertEquals(userId, createdEmailVerification.userId());
        assertEquals(email, createdEmailVerification.email());
        assertEquals(type, createdEmailVerification.type());
        assertTrue(jpaEmailVerificationRepository.existsById(createdEmailVerification.id()));
    }
}