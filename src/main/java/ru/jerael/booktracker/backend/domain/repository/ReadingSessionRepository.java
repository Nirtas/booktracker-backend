package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession;
import java.util.List;
import java.util.UUID;

public interface ReadingSessionRepository {
    List<ReadingSession> findAllByAttemptId(UUID attemptId);

    ReadingSession save(ReadingSession readingSession);
}
