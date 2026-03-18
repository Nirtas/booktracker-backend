package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaReadingAttemptRepository extends JpaRepository<ReadingAttemptEntity, UUID> {
    List<ReadingAttemptEntity> findAllByBookId(UUID bookId);

    List<ReadingAttemptEntity> findAllByBookIdAndStatus(UUID bookId, BookStatus status);
}
