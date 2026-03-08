package ru.jerael.booktracker.backend.web.dto.user;

public record UserCreationRequest(
    String email,
    String password
) {}
