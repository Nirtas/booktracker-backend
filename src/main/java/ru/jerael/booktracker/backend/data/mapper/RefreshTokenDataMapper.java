package ru.jerael.booktracker.backend.data.mapper;

import ru.jerael.booktracker.backend.data.annotation.DataMapper;
import ru.jerael.booktracker.backend.data.db.entity.RefreshTokenEntity;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;

@DataMapper
public class RefreshTokenDataMapper {
    public RefreshTokenEntity toEntity(RefreshToken refreshToken) {
        if (refreshToken == null) return null;

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(refreshToken.id());

        UserEntity userEntity = new UserEntity();
        userEntity.setId(refreshToken.userId());
        entity.setUser(userEntity);

        entity.setTokenHash(refreshToken.tokenHash());
        entity.setExpiresAt(refreshToken.expiresAt());
        return entity;
    }

    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) return null;

        return new RefreshToken(
            entity.getId(),
            entity.getUser().getId(),
            entity.getTokenHash(),
            entity.getExpiresAt()
        );
    }
}
