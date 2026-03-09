package ru.jerael.booktracker.backend.domain.service.token;

import ru.jerael.booktracker.backend.domain.model.auth.GeneratedToken;
import java.util.Map;
import java.util.UUID;

public interface IdentityTokenProvider {
    String generateAccessToken(UUID userId);

    GeneratedToken generateRefreshToken(UUID userId);

    Map<String, Object> extractClaims(String token);
}
