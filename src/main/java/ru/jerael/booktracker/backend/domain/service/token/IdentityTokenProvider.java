package ru.jerael.booktracker.backend.domain.service.token;

import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenClaims;

public interface IdentityTokenProvider {
    String encode(IdentityTokenClaims data);

    IdentityTokenClaims decode(String token);
}
