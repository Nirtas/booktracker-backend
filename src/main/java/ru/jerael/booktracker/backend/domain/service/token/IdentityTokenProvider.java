package ru.jerael.booktracker.backend.domain.service.token;

import ru.jerael.booktracker.backend.domain.model.auth.GeneratedToken;
import java.util.Map;

public interface IdentityTokenProvider {
    String generateAccessToken(Map<String, Object> claims);

    GeneratedToken generateRefreshToken();

    Map<String, Object> extractClaims(String token);
}
