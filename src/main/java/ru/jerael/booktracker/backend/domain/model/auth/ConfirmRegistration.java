package ru.jerael.booktracker.backend.domain.model.auth;

import java.util.UUID;

public record ConfirmRegistration(
    UUID userId,
    String token
) {}
