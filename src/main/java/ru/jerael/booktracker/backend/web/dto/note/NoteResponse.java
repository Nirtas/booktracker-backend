package ru.jerael.booktracker.backend.web.dto.note;

import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.UUID;

public record NoteResponse(
    UUID id,
    String type,
    @Nullable String textContent,
    @Nullable String fileName,
    int pageNumber,
    Instant createdAt
) {}
