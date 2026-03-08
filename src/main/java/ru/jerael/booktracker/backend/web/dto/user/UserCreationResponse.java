package ru.jerael.booktracker.backend.web.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserCreationResponse(
    UUID userId,
    String email,
    Instant expiresAt
) {}
