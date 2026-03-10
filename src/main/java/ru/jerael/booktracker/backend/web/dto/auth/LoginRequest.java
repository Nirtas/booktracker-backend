package ru.jerael.booktracker.backend.web.dto.auth;

public record LoginRequest(
    String email,
    String password
) {}
