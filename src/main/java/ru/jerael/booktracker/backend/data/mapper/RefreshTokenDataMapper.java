package ru.jerael.booktracker.backend.data.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.RefreshTokenEntity;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;

@Component
public class RefreshTokenDataMapper {
    public RefreshTokenEntity toEntity(RefreshToken refreshToken) {
        if (refreshToken == null) return null;

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(refreshToken.id());
        entity.setUserId(refreshToken.userId());
        entity.setTokenHash(refreshToken.tokenHash());
        entity.setExpiresAt(refreshToken.expiresAt());
        return entity;
    }

    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) return null;

        return new RefreshToken(
            entity.getId(),
            entity.getUserId(),
            entity.getTokenHash(),
            entity.getExpiresAt()
        );
    }
}
