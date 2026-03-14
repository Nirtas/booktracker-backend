package ru.jerael.booktracker.backend.domain.model.auth;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(
    UUID id,
    UUID userId,
    String tokenHash,
    Instant expiresAt
) {}
