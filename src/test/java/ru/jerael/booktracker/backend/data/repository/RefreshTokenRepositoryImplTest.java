package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.entity.RefreshTokenEntity;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaRefreshTokenRepository;
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository;
import ru.jerael.booktracker.backend.data.mapper.RefreshTokenDataMapper;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@DataJpaTest
@Import({RefreshTokenRepositoryImpl.class, RefreshTokenDataMapper.class})
class RefreshTokenRepositoryImplTest {

    @Autowired
    private RefreshTokenRepositoryImpl refreshTokenRepository;

    @Autowired
    private JpaRefreshTokenRepository jpaRefreshTokenRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

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

    @Test
    void findAllByUserId() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userId + "@example.com");
        userEntity.setPasswordHash("password hash");
        userEntity.setVerified(true);
        userEntity.setCreatedAt(Instant.now());
        UUID userId2 = jpaUserRepository.save(userEntity).getId();

        saveEntity(userId, tokenHash);
        saveEntity(userId2, "token hash 2");
        saveEntity(userId2, "token hash 3");

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(userId2);

        assertEquals(2, refreshTokens.size());
        assertThat(refreshTokens).allSatisfy(refreshToken -> {
            assertEquals(userId2, refreshToken.userId());
            assertThat(refreshToken.tokenHash()).startsWith("token hash ");
        });
    }

    private void saveEntity(UUID userId, String hash) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUserId(userId);
        entity.setTokenHash(hash);
        entity.setExpiresAt(expiresAt);
        jpaRefreshTokenRepository.save(entity);
    }
}