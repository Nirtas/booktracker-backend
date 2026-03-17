package ru.jerael.booktracker.backend.domain.model.author;

import java.util.UUID;

public record Author(
    UUID id,
    String fullName
) {}
