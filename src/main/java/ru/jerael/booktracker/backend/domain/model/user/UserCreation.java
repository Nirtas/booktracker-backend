package ru.jerael.booktracker.backend.domain.model.user;

public record UserCreation(
    String email,
    String password
) {}
