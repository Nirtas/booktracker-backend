package ru.jerael.booktracker.backend.domain.model.auth;

import java.time.Instant;
import java.util.UUID;

public record ResendVerificationResult(
    UUID userId,
    String email,
    Instant expiresAt
) {}
