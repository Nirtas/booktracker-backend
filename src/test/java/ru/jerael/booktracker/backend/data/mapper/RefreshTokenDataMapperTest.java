package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.RefreshTokenEntity;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;
import java.time.Instant;
import java.util.UUID;
import static org.junit.Assert.assertEquals;

class RefreshTokenDataMapperTest {
    private final RefreshTokenDataMapper refreshTokenDataMapper = new RefreshTokenDataMapper();

    private final UUID id = UUID.fromString("2119fe3a-4067-4557-99c6-c28addc2cdd9");
    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String tokenHash = "token hash";
    private final Instant expiresAt = Instant.ofEpochMilli(1771249999347L);

    @Test
    void toEntity() {
        RefreshToken refreshToken = new RefreshToken(id, userId, tokenHash, expiresAt);

        RefreshTokenEntity entity = refreshTokenDataMapper.toEntity(refreshToken);

        assertEquals(id, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals(tokenHash, entity.getTokenHash());
        assertEquals(expiresAt, entity.getExpiresAt());
    }
}