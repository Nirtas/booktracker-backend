package ru.jerael.booktracker.backend.api.dto.book;

import jakarta.annotation.Nullable;
import ru.jerael.booktracker.backend.api.dto.genre.GenreResponse;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record BookResponse(
    UUID id,
    String title,
    String author,
    @Nullable String coverUrl,
    String status,
    Instant createdAt,
    Set<GenreResponse> genres
) {}
