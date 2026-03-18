package ru.jerael.booktracker.backend.data.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity;
import ru.jerael.booktracker.backend.data.db.entity.ReadingSessionEntity;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReadingAttemptDataMapper {
    private final ReadingSessionDataMapper readingSessionDataMapper;

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

        if (readingAttempt.sessions() != null) {
            entity.setSessions(
                readingAttempt.sessions().stream()
                    .map(readingSession -> {
                        ReadingSessionEntity sessionEntity = readingSessionDataMapper.toEntity(readingSession);
                        sessionEntity.setReadingAttempt(entity);
                        return sessionEntity;
                    })
                    .collect(Collectors.toList())
            );
        }

        return entity;
    }

    public ReadingAttempt toDomain(ReadingAttemptEntity entity) {
        if (entity == null) return null;

        return new ReadingAttempt(
            entity.getId(),
            entity.getBook().getId(),
            entity.getStatus(),
            entity.getStartedAt(),
            entity.getFinishedAt(),
            entity.getSessions().stream().map(readingSessionDataMapper::toDomain).toList()
        );
    }
}
