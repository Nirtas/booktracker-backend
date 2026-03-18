package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaReadingAttemptRepository;
import ru.jerael.booktracker.backend.data.mapper.ReadingAttemptDataMapper;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;
import ru.jerael.booktracker.backend.domain.repository.ReadingAttemptRepository;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReadingAttemptRepositoryImpl implements ReadingAttemptRepository {
    private final JpaReadingAttemptRepository jpaReadingAttemptRepository;
    private final ReadingAttemptDataMapper readingAttemptDataMapper;

    @Override
    public List<ReadingAttempt> findAllByBookId(UUID bookId) {
        return jpaReadingAttemptRepository.findAllByBookId(bookId).stream().map(readingAttemptDataMapper::toDomain)
            .toList();
    }

    @Override
    public List<ReadingAttempt> findAllByBookIdAndStatus(UUID bookId, BookStatus status) {
        return jpaReadingAttemptRepository.findAllByBookIdAndStatus(bookId, status).stream()
            .map(readingAttemptDataMapper::toDomain).toList();
    }

    @Override
    public ReadingAttempt save(ReadingAttempt readingAttempt) {
        ReadingAttemptEntity entity = readingAttemptDataMapper.toEntity(readingAttempt);
        ReadingAttemptEntity savedEntity = jpaReadingAttemptRepository.save(entity);
        return readingAttemptDataMapper.toDomain(savedEntity);
    }
}
