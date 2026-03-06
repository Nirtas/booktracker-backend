package ru.jerael.booktracker.backend.domain.model.user;

import java.time.Instant;
import java.util.UUID;

public record User(
    UUID id,
    String email,
    String passwordHash,
    boolean isVerified,
    Instant createdAt
) {}
