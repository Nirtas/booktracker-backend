package ru.jerael.booktracker.backend.domain.service.token;

import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import java.util.UUID;

public interface AuthTokenService {
    TokenPair issueTokens(UUID userId);
}
