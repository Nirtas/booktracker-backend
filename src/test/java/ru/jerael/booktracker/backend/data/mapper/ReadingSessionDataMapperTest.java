package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity;
import ru.jerael.booktracker.backend.data.db.entity.ReadingSessionEntity;
import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReadingSessionDataMapperTest {
    private final ReadingSessionDataMapper readingSessionDataMapper = new ReadingSessionDataMapper();

    private final UUID id = UUID.fromString("4f38de27-b492-4b27-96ea-32331bc82598");
    private final UUID attemptId = UUID.fromString("baf246c3-5f17-495f-a8be-f724dcc8d485");
    private final int startPage = 10;
    private final int endPage = 25;
    private final Instant startedAt = Instant.now().minusSeconds(1000);
    private final Instant finishedAt = Instant.now();

    @Test
    void toEntity() {
        ReadingSession readingSession = new ReadingSession(id, attemptId, startPage, endPage, startedAt, finishedAt);

        ReadingSessionEntity entity = readingSessionDataMapper.toEntity(readingSession);

        assertEquals(id, entity.getId());
        assertEquals(attemptId, entity.getReadingAttempt().getId());
        assertEquals(startPage, entity.getStartPage());
        assertEquals(endPage, entity.getEndPage());
        assertEquals(startedAt, entity.getStartedAt());
        assertEquals(finishedAt, entity.getFinishedAt());
    }

    @Test
    void toDomain() {
        ReadingAttemptEntity attemptEntity = new ReadingAttemptEntity();
        attemptEntity.setId(attemptId);

        ReadingSessionEntity readingSession = new ReadingSessionEntity();
        readingSession.setId(id);
        readingSession.setReadingAttempt(attemptEntity);
        readingSession.setStartPage(startPage);
        readingSession.setEndPage(endPage);
        readingSession.setStartedAt(startedAt);
        readingSession.setFinishedAt(finishedAt);

        ReadingSession result = readingSessionDataMapper.toDomain(readingSession);

        assertEquals(id, result.id());
        assertEquals(attemptId, result.attemptId());
        assertEquals(startPage, result.startPage());
        assertEquals(endPage, result.endPage());
        assertEquals(startedAt, result.startedAt());
        assertEquals(finishedAt, result.finishedAt());
    }
}