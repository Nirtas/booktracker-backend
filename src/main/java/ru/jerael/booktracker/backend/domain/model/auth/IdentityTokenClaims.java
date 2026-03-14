package ru.jerael.booktracker.backend.domain.model.auth;

import java.time.Instant;
import java.util.UUID;

public record IdentityTokenClaims(
    UUID userId,
    IdentityTokenType type,
    String issuer,
    Instant issuedAt,
    Instant expiresAt
) {}
