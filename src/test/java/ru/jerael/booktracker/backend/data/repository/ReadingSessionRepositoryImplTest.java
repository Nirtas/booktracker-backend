package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity;
import ru.jerael.booktracker.backend.data.db.entity.ReadingSessionEntity;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository;
import ru.jerael.booktracker.backend.data.db.repository.JpaReadingAttemptRepository;
import ru.jerael.booktracker.backend.data.db.repository.JpaReadingSessionRepository;
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository;
import ru.jerael.booktracker.backend.data.mapper.ReadingSessionDataMapper;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({ReadingSessionRepositoryImpl.class, ReadingSessionDataMapper.class})
class ReadingSessionRepositoryImplTest {

    @Autowired
    private ReadingSessionRepositoryImpl readingSessionRepository;

    @Autowired
    private JpaReadingSessionRepository jpaReadingSessionRepository;

    @Autowired
    private JpaReadingAttemptRepository jpaReadingAttemptRepository;

    @Autowired
    private JpaBookRepository jpaBookRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    private final int startPage = 10;
    private final int endPage = 25;
    private final Instant startedAt = Instant.now().minusSeconds(1000);
    private final Instant finishedAt = Instant.now();
    private final BookStatus status = BookStatus.READING;

    @Test
    void findAllByAttemptId_ShouldReturnListOfSessionsInAttempt() {
        UUID userId = saveUser();
        BookEntity book = saveBook(userId);
        ReadingAttemptEntity readingAttempt = saveReadingAttempt(book, status);

        ReadingSessionEntity entity1 = buildReadingSessionEntity(readingAttempt, 0, 10);
        jpaReadingSessionRepository.save(entity1);

        ReadingSessionEntity entity2 = buildReadingSessionEntity(readingAttempt, 10, 25);
        jpaReadingSessionRepository.save(entity2);

        List<ReadingSession> result = readingSessionRepository.findAllByAttemptId(readingAttempt.getId());

        assertEquals(2, result.size());
        assertEquals(0, result.get(0).startPage());
        assertEquals(10, result.get(0).endPage());
    }

    @Test
    void save_WhenIdIsNull_ShouldInsertNewReadingSession() {
        UUID userId = saveUser();
        BookEntity book = saveBook(userId);
        ReadingAttemptEntity readingAttempt = saveReadingAttempt(book, status);
        ReadingSession readingSession = buildReadingSession(null, readingAttempt.getId(), startPage, endPage);

        ReadingSession result = readingSessionRepository.save(readingSession);

        assertNotNull(result.id());
        assertEquals(readingAttempt.getId(), result.attemptId());
        assertEquals(startPage, result.startPage());
    }

    @Test
    void save_WhenIdIsPresent_ShouldUpdateExistingReadingSession() {
        UUID userId = saveUser();
        BookEntity book = saveBook(userId);
        ReadingAttemptEntity readingAttempt = saveReadingAttempt(book, status);
        ReadingSession readingSession = buildReadingSession(null, readingAttempt.getId(), startPage, endPage);

        UUID savedId = readingSessionRepository.save(readingSession).id();

        ReadingSession updatedReadingSession = buildReadingSession(savedId, readingAttempt.getId(), 50, 100);

        ReadingSession result = readingSessionRepository.save(updatedReadingSession);

        assertEquals(savedId, result.id());
        assertEquals(readingAttempt.getId(), result.attemptId());
        assertEquals(50, result.startPage());
    }

    private BookEntity saveBook(UUID userId) {
        BookEntity entity = new BookEntity();
        entity.setUserId(userId);
        entity.setTitle("title");
        entity.setAuthor("author");
        entity.setCoverFileName(null);
        entity.setStatus(BookStatus.WANT_TO_READ);
        entity.setCreatedAt(Instant.now());
        entity.setGenres(Collections.emptySet());
        return jpaBookRepository.save(entity);
    }

    private UUID saveUser() {
        UserEntity entity = new UserEntity();
        entity.setEmail("test@example.com");
        entity.setPasswordHash("password hash");
        entity.setVerified(true);
        entity.setCreatedAt(Instant.now());
        return jpaUserRepository.save(entity).getId();
    }

    private ReadingAttemptEntity saveReadingAttempt(BookEntity book, BookStatus status) {
        ReadingAttemptEntity entity = new ReadingAttemptEntity();
        entity.setBook(book);
        entity.setStatus(status);
        entity.setStartedAt(startedAt);
        entity.setFinishedAt(finishedAt);
        return jpaReadingAttemptRepository.save(entity);
    }

    private ReadingSessionEntity buildReadingSessionEntity(
        ReadingAttemptEntity attemptEntity,
        int startPage,
        int endPage
    ) {
        ReadingSessionEntity entity = new ReadingSessionEntity();
        entity.setReadingAttempt(attemptEntity);
        entity.setStartPage(startPage);
        entity.setEndPage(endPage);
        entity.setStartedAt(startedAt);
        entity.setFinishedAt(finishedAt);
        return entity;
    }

    private ReadingSession buildReadingSession(UUID id, UUID attemptId, int startPage, int endPage) {
        return new ReadingSession(
            id,
            attemptId,
            startPage,
            endPage,
            startedAt,
            finishedAt
        );
    }
}