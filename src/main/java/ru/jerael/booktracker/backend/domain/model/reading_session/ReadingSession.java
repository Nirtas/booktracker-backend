package ru.jerael.booktracker.backend.domain.model.reading_session;

import java.time.Instant;
import java.util.UUID;

public record ReadingSession(
    UUID id,
    UUID attemptId,
    int startPage,
    int endPage,
    Instant startedAt,
    Instant finishedAt
) {}
