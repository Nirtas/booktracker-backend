package ru.jerael.booktracker.backend.domain.model.note;

import java.time.Instant;
import java.util.UUID;

public record Note(
    UUID id,
    UUID bookId,
    NoteType type,
    String textContent,
    String fileName,
    int pageNumber,
    Instant createdAt
) {}
