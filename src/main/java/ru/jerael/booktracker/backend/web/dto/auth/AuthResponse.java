package ru.jerael.booktracker.backend.web.dto.auth;

public record AuthResponse(
    String accessToken,
    String refreshToken
) {}
