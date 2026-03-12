package ru.jerael.booktracker.backend.domain.service.token;

import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenClaims;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType;
import ru.jerael.booktracker.backend.domain.model.auth.TokenPair;
import java.util.UUID;

public interface AuthTokenService {
    TokenPair issueTokens(UUID userId);

    IdentityTokenClaims verifyToken(String token, IdentityTokenType expectedTokenType);
}
