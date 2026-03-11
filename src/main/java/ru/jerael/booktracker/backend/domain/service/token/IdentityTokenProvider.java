package ru.jerael.booktracker.backend.domain.service.token;

import ru.jerael.booktracker.backend.domain.model.auth.GeneratedToken;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenClaims;
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType;
import java.util.Map;

public interface IdentityTokenProvider {
    @Deprecated
    GeneratedToken generateToken(Map<String, Object> claims, IdentityTokenType tokenType);

    @Deprecated
    Map<String, Object> extractClaims(String token, IdentityTokenType expectedType);

    String encode(IdentityTokenClaims data);

    IdentityTokenClaims decode(String token);
}
