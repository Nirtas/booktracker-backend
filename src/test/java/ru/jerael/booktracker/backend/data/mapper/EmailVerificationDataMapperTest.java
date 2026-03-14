package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.EmailVerificationEntity;
import ru.jerael.booktracker.backend.domain.model.verification.EmailVerification;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailVerificationDataMapperTest {
    private final EmailVerificationDataMapper emailVerificationDataMapper = new EmailVerificationDataMapper();

    private final UUID id = UUID.fromString("d756300f-6a7f-45df-96c7-55977999f7c9");
    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String email = "test@example.com";
    private final VerificationType type = VerificationType.REGISTRATION;
    private final String token = "token";
    private final Instant expiresAt = Instant.ofEpochMilli(1771249999347L);
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    @Test
    void toDomain() {
        EmailVerificationEntity entity = new EmailVerificationEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setEmail(email);
        entity.setType(type);
        entity.setToken(token);
        entity.setExpiresAt(expiresAt);
        entity.setCreatedAt(createdAt);

        EmailVerification emailVerification = emailVerificationDataMapper.toDomain(entity);

        assertEquals(id, emailVerification.id());
        assertEquals(userId, emailVerification.userId());
        assertEquals(email, emailVerification.email());
        assertEquals(type, emailVerification.type());
        assertEquals(token, emailVerification.token());
        assertEquals(expiresAt, emailVerification.expiresAt());
        assertEquals(createdAt, emailVerification.createdAt());
    }

    @Test
    void toEntity() {
        EmailVerification emailVerification =
            new EmailVerification(id, userId, email, type, token, expiresAt, createdAt);

        EmailVerificationEntity entity = emailVerificationDataMapper.toEntity(emailVerification);

        assertEquals(id, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals(email, entity.getEmail());
        assertEquals(type, entity.getType());
        assertEquals(token, entity.getToken());
        assertEquals(expiresAt, entity.getExpiresAt());
        assertEquals(createdAt, entity.getCreatedAt());
    }
}