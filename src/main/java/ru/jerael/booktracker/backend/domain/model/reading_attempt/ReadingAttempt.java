package ru.jerael.booktracker.backend.domain.model.reading_attempt;

import jakarta.annotation.Nullable;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import java.time.Instant;
import java.util.UUID;

public record ReadingAttempt(
    UUID id,
    UUID bookId,
    BookStatus status,
    Instant startedAt,
    @Nullable Instant finishedAt
) {}
