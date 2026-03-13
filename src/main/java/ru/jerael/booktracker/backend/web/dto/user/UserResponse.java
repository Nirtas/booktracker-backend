package ru.jerael.booktracker.backend.web.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID userId,
    String email,
    boolean isVerified,
    Instant createdAt
) {}
