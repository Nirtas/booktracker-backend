package ru.jerael.booktracker.backend.domain.model.verification;

import java.time.Instant;
import java.util.UUID;

public record EmailVerification(
    UUID id,
    UUID userId,
    String email,
    VerificationType type,
    String token,
    Instant expiresAt,
    Instant createdAt
) {}
