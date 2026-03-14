package ru.jerael.booktracker.backend.domain.model.auth;

public record UserLogin(
    String email,
    String password
) {}
