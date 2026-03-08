package ru.jerael.booktracker.backend.domain.model.user;

import java.time.Instant;
import java.util.UUID;

public record UserCreationResult(
    UUID userId,
    String email,
    Instant expiresAt
) {}
