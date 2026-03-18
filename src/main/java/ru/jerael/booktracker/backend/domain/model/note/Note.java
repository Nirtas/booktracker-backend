package ru.jerael.booktracker.backend.domain.model.note;

import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.UUID;

public record Note(
    UUID id,
    UUID bookId,
    NoteType type,
    @Nullable String textContent,
    @Nullable String fileName,
    int pageNumber,
    Instant createdAt
) {}
