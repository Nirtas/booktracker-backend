package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository;
import ru.jerael.booktracker.backend.data.db.repository.JpaReadingAttemptRepository;
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository;
import ru.jerael.booktracker.backend.data.mapper.ReadingAttemptDataMapper;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import({ReadingAttemptRepositoryImpl.class, ReadingAttemptDataMapper.class})
class ReadingAttemptRepositoryImplTest {

    @Autowired
    private ReadingAttemptRepositoryImpl readingAttemptRepository;

    @Autowired
    private JpaReadingAttemptRepository jpaReadingAttemptRepository;

    @Autowired
    private JpaBookRepository jpaBookRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    private final UUID userId = UUID.fromString("57702775-4f01-4849-97e5-8c7456e01b5b");
    private final UUID id = UUID.fromString("4f38de27-b492-4b27-96ea-32331bc82598");
    private final UUID bookId = UUID.fromString("baf246c3-5f17-495f-a8be-f724dcc8d485");
    private final BookStatus status = BookStatus.READING;
    private final Instant startedAt = Instant.now().minusSeconds(1000);
    private final Instant finishedAt = Instant.now();

    @Test
    void findAllByBookId_ShouldReturnListOfReadingAttemptsOfBook() {
        UUID userId = saveUser();
        BookEntity book1 = saveBook(userId);
        BookEntity book2 = saveBook(userId);

        ReadingAttemptEntity entity1 = buildReadingAttemptEntity(book1, BookStatus.READING);
        jpaReadingAttemptRepository.save(entity1);

        ReadingAttemptEntity entity2 = buildReadingAttemptEntity(book1, BookStatus.READ);
        jpaReadingAttemptRepository.save(entity2);

        ReadingAttemptEntity entity3 = buildReadingAttemptEntity(book2, BookStatus.WANT_TO_READ);
        jpaReadingAttemptRepository.save(entity3);

        List<ReadingAttempt> attempts = readingAttemptRepository.findAllByBookId(book1.getId());

        assertEquals(2, attempts.size());
    }

    @Test
    void findAllByBookIdAndStatus_ShouldReturnListOfReadingAttemptsOfBookWithStatus() {
        UUID userId = saveUser();
        BookEntity book = saveBook(userId);

        ReadingAttemptEntity entity1 = buildReadingAttemptEntity(book, BookStatus.READING);
        jpaReadingAttemptRepository.save(entity1);

        ReadingAttemptEntity entity2 = buildReadingAttemptEntity(book, BookStatus.READ);
        jpaReadingAttemptRepository.save(entity2);

        List<ReadingAttempt> attempts =
            readingAttemptRepository.findAllByBookIdAndStatus(book.getId(), BookStatus.READING);

        assertEquals(1, attempts.size());
        assertEquals(BookStatus.READING, attempts.get(0).status());
    }

    @Test
    void save_WhenIdIsNull_ShouldInsertNewReadingAttempt() {
        UUID userId = saveUser();
        BookEntity book = saveBook(userId);
        ReadingAttempt attempt = buildReadingAttempt(null, book.getId(), status);

        ReadingAttempt result = readingAttemptRepository.save(attempt);

        assertNotNull(result.id());
        assertEquals(book.getId(), result.bookId());
        assertEquals(status, result.status());
        assertNotNull(result.startedAt());
    }

    @Test
    void save_WhenIdIsPresent_ShouldUpdateExistingReadingAttempt() {
        UUID userId = saveUser();
        BookEntity book = saveBook(userId);
        ReadingAttempt attempt = buildReadingAttempt(null, book.getId(), status);

        UUID savedId = readingAttemptRepository.save(attempt).id();

        ReadingAttempt updatedAttempt = buildReadingAttempt(savedId, book.getId(), BookStatus.WANT_TO_READ);

        ReadingAttempt result = readingAttemptRepository.save(updatedAttempt);

        assertEquals(savedId, result.id());
        assertEquals(BookStatus.WANT_TO_READ, result.status());
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

    private ReadingAttemptEntity buildReadingAttemptEntity(BookEntity book, BookStatus status) {
        ReadingAttemptEntity entity = new ReadingAttemptEntity();
        entity.setBook(book);
        entity.setStatus(status);
        entity.setStartedAt(startedAt);
        entity.setFinishedAt(finishedAt);
        return entity;
    }

    private ReadingAttempt buildReadingAttempt(UUID id, UUID bookId, BookStatus status) {
        return new ReadingAttempt(
            id,
            bookId,
            status,
            startedAt,
            finishedAt
        );
    }
}