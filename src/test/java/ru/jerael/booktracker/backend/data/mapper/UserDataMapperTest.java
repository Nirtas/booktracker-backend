package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.domain.model.user.User;
import java.time.Instant;
import java.util.UUID;
import static org.junit.Assert.assertEquals;

class UserDataMapperTest {
    private final UserDataMapper userDataMapper = new UserDataMapper();

    private final UUID id = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String email = "test@example.com";
    private final String passwordHash = "password hash";
    private final boolean isVerified = true;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    @Test
    void toDomain() {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setEmail(email);
        entity.setPasswordHash(passwordHash);
        entity.setVerified(isVerified);
        entity.setCreatedAt(createdAt);

        User user = userDataMapper.toDomain(entity);

        assertEquals(id, user.id());
        assertEquals(email, user.email());
        assertEquals(passwordHash, user.passwordHash());
        assertEquals(isVerified, user.isVerified());
        assertEquals(createdAt, user.createdAt());
    }

    @Test
    void toEntity() {
        User user = new User(id, email, passwordHash, isVerified, createdAt);

        UserEntity entity = userDataMapper.toEntity(user);

        assertEquals(id, entity.getId());
        assertEquals(email, entity.getEmail());
        assertEquals(passwordHash, entity.getPasswordHash());
        assertEquals(isVerified, entity.isVerified());
        assertEquals(createdAt, entity.getCreatedAt());
    }
}