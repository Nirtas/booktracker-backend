package ru.jerael.booktracker.backend.domain.model.reading_attempt;

import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReadingAttempt(
    UUID id,
    UUID bookId,
    BookStatus status,
    Instant startedAt,
    Instant finishedAt,
    List<ReadingSession> sessions
) {}
