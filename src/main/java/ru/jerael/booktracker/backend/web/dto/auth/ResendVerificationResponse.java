package ru.jerael.booktracker.backend.web.dto.auth;

import java.time.Instant;
import java.util.UUID;

public record ResendVerificationResponse(
    UUID userId,
    String email,
    Instant expiresAt
) {}
