package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReadingAttemptDataMapperTest {
    private final ReadingAttemptDataMapper readingAttemptDataMapper = new ReadingAttemptDataMapper();

    private final UUID userId = UUID.fromString("57702775-4f01-4849-97e5-8c7456e01b5b");
    private final UUID id = UUID.fromString("4f38de27-b492-4b27-96ea-32331bc82598");
    private final UUID bookId = UUID.fromString("baf246c3-5f17-495f-a8be-f724dcc8d485");
    private final BookStatus status = BookStatus.READING;
    private final Instant startedAt = Instant.now().minusSeconds(1000);
    private final Instant finishedAt = Instant.now();

    @Test
    void toEntity() {
        ReadingAttempt readingAttempt = new ReadingAttempt(id, bookId, status, startedAt, finishedAt);

        ReadingAttemptEntity entity = readingAttemptDataMapper.toEntity(readingAttempt);

        assertEquals(id, entity.getId());
        assertEquals(bookId, entity.getBook().getId());
        assertEquals(status, entity.getStatus());
        assertEquals(startedAt, entity.getStartedAt());
        assertEquals(finishedAt, entity.getFinishedAt());
    }

    @Test
    void toDomain() {
        BookEntity book = new BookEntity();
        book.setId(bookId);
        book.setUserId(userId);
        book.setTitle("title");
        book.setAuthor("author");
        book.setCoverFileName(null);
        book.setStatus(BookStatus.WANT_TO_READ);
        book.setCreatedAt(Instant.now());
        book.setGenres(Collections.emptySet());

        ReadingAttemptEntity entity = new ReadingAttemptEntity();
        entity.setId(id);
        entity.setBook(book);
        entity.setStatus(status);
        entity.setStartedAt(startedAt);
        entity.setFinishedAt(finishedAt);

        ReadingAttempt result = readingAttemptDataMapper.toDomain(entity);

        assertEquals(id, result.id());
        assertEquals(bookId, result.bookId());
        assertEquals(status, result.status());
        assertEquals(startedAt, result.startedAt());
        assertEquals(finishedAt, result.finishedAt());
    }
}