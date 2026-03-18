package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;
import java.util.List;
import java.util.UUID;

public interface ReadingAttemptRepository {
    List<ReadingAttempt> findAllByBookId(UUID bookId);

    List<ReadingAttempt> findAllByBookIdAndStatus(UUID bookId, BookStatus status);

    ReadingAttempt save(ReadingAttempt readingAttempt);
}
