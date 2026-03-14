package ru.jerael.booktracker.backend.domain.model.auth;

public record TokenPair(
    String accessToken,
    String refreshToken
) {}
