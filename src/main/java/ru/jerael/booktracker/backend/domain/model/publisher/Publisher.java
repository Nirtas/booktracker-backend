package ru.jerael.booktracker.backend.domain.model.publisher;

import java.util.UUID;

public record Publisher(
    UUID id,
    String name
) {}
