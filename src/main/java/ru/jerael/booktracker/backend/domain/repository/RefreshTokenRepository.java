package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;
import java.util.List;
import java.util.UUID;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);

    List<RefreshToken> findAllByUserId(UUID userId);

    void deleteById(UUID id);
}
