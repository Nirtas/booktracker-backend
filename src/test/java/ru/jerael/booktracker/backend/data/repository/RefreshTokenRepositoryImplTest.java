package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.entity.RefreshTokenEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaRefreshTokenRepository;
import ru.jerael.booktracker.backend.data.mapper.RefreshTokenDataMapper;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.assertEquals;

@DataJpaTest
@Import({RefreshTokenRepositoryImpl.class, RefreshTokenDataMapper.class})
class RefreshTokenRepositoryImplTest {

    @Autowired
    private RefreshTokenRepositoryImpl refreshTokenRepository;

    @Autowired
    private JpaRefreshTokenRepository jpaRefreshTokenRepository;

    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String tokenHash = "token hash";
    private final Instant expiresAt = Instant.ofEpochMilli(1771249999347L);

    @Test
    void save() {
        RefreshToken refreshToken = new RefreshToken(null, userId, tokenHash, expiresAt);

        refreshTokenRepository.save(refreshToken);

        List<RefreshTokenEntity> entities = jpaRefreshTokenRepository.findAll();

        assertEquals(1, entities.size());

        RefreshTokenEntity entity = entities.get(0);
        assertEquals(userId, entity.getUserId());
        assertEquals(tokenHash, entity.getTokenHash());
        assertEquals(expiresAt, entity.getExpiresAt());
    }
}