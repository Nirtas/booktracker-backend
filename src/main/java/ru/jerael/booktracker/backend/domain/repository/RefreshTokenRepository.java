package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);
}
