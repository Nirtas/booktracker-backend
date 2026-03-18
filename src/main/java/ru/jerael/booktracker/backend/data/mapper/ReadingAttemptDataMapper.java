package ru.jerael.booktracker.backend.data.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;

@Component
public class ReadingAttemptDataMapper {
    public ReadingAttemptEntity toEntity(ReadingAttempt readingAttempt) {
        if (readingAttempt == null) return null;

        ReadingAttemptEntity entity = new ReadingAttemptEntity();
        entity.setId(readingAttempt.id());

        BookEntity bookEntity = new BookEntity();
        bookEntity.setId(readingAttempt.bookId());
        entity.setBook(bookEntity);

        entity.setStatus(readingAttempt.status());
        entity.setStartedAt(readingAttempt.startedAt());
        entity.setFinishedAt(readingAttempt.finishedAt());
        return entity;
    }

    public ReadingAttempt toDomain(ReadingAttemptEntity entity) {
        if (entity == null) return null;

        return new ReadingAttempt(
            entity.getId(),
            entity.getBook().getId(),
            entity.getStatus(),
            entity.getStartedAt(),
            entity.getFinishedAt()
        );
    }
}
