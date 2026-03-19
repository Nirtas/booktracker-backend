package ru.jerael.booktracker.backend.web.dto.reading_session;

import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.UUID;

public record ReadingSessionResponse(
    UUID id,
    int startPage,
    int endPage,
    Instant startedAt,
    @Nullable Instant finishedAt
) {}
