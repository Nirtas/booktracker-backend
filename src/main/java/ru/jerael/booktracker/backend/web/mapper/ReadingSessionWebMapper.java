package ru.jerael.booktracker.backend.web.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession;
import ru.jerael.booktracker.backend.web.dto.reading_session.ReadingSessionResponse;

@Component
public class ReadingSessionWebMapper {
    public ReadingSessionResponse toResponse(ReadingSession readingSession) {
        if (readingSession == null) return null;

        return new ReadingSessionResponse(
            readingSession.id(),
            readingSession.startPage(),
            readingSession.endPage(),
            readingSession.startedAt(),
            readingSession.finishedAt()
        );
    }
}
