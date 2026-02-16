package ru.jerael.booktracker.backend.domain.model.book;

import jakarta.annotation.Nullable;
import ru.jerael.booktracker.backend.domain.model.Genre;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record Book(
    UUID id,
    String title,
    String author,
    @Nullable String coverUrl,
    BookStatus status,
    Instant createdAt,
    Set<Genre> genres
) {}
