package ru.jerael.booktracker.backend.web.dto.auth;

import java.util.UUID;

public record ResendVerificationRequest(
    UUID userId,
    String type
) {}
