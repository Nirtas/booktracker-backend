package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.RefreshTokenEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaRefreshTokenRepository;
import ru.jerael.booktracker.backend.data.mapper.RefreshTokenDataMapper;
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
    private final RefreshTokenDataMapper refreshTokenDataMapper;

    @Override
    public void save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = refreshTokenDataMapper.toEntity(refreshToken);
        jpaRefreshTokenRepository.save(entity);
    }

    @Override
    public List<RefreshToken> findAllByUserId(UUID userId) {
        return jpaRefreshTokenRepository.findAllByUserId(userId).stream().map(refreshTokenDataMapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRefreshTokenRepository.deleteById(id);
    }
}
