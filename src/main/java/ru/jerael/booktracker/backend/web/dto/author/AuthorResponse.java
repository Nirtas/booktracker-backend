package ru.jerael.booktracker.backend.web.dto.author;

import java.util.UUID;

public record AuthorResponse(
    UUID id,
    String fullName
) {}
