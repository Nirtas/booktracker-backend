package ru.jerael.booktracker.backend.data.mapper;

import ru.jerael.booktracker.backend.data.annotation.DataMapper;
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity;
import ru.jerael.booktracker.backend.data.db.entity.ReadingSessionEntity;
import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession;

@DataMapper
public class ReadingSessionDataMapper {
    public ReadingSessionEntity toEntity(ReadingSession readingSession) {
        if (readingSession == null) return null;

        ReadingSessionEntity entity = new ReadingSessionEntity();
        entity.setId(readingSession.id());

        ReadingAttemptEntity readingAttempt = new ReadingAttemptEntity();
        readingAttempt.setId(readingSession.attemptId());
        entity.setReadingAttempt(readingAttempt);

        entity.setStartPage(readingSession.startPage());
        entity.setEndPage(readingSession.endPage());
        entity.setStartedAt(readingSession.startedAt());
        entity.setFinishedAt(readingSession.finishedAt());
        return entity;
    }

    public ReadingSession toDomain(ReadingSessionEntity entity) {
        if (entity == null) return null;

        return new ReadingSession(
            entity.getId(),
            entity.getReadingAttempt().getId(),
            entity.getStartPage(),
            entity.getEndPage(),
            entity.getStartedAt(),
            entity.getFinishedAt()
        );
    }
}
