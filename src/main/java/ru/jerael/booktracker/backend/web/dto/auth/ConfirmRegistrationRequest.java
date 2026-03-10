package ru.jerael.booktracker.backend.web.dto.auth;

import java.util.UUID;

public record ConfirmRegistrationRequest(
    UUID userId,
    String token
) {}
