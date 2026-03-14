package ru.jerael.booktracker.backend.web.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record BookResponse(
    UUID id,
    String title,
    String author,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @Nullable
    String coverUrl,

    String status,
    Instant createdAt,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    Set<GenreResponse> genres
) {}
