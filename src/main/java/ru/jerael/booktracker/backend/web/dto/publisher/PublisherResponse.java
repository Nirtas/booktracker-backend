package ru.jerael.booktracker.backend.web.dto.publisher;

import java.util.UUID;

public record PublisherResponse(
    UUID id,
    String name
) {}
