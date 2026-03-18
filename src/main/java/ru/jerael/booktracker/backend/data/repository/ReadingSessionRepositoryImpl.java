package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.ReadingSessionEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaReadingSessionRepository;
import ru.jerael.booktracker.backend.data.mapper.ReadingSessionDataMapper;
import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession;
import ru.jerael.booktracker.backend.domain.repository.ReadingSessionRepository;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReadingSessionRepositoryImpl implements ReadingSessionRepository {
    private final JpaReadingSessionRepository jpaReadingSessionRepository;
    private final ReadingSessionDataMapper readingSessionDataMapper;

    @Override
    public List<ReadingSession> findAllByAttemptId(UUID attemptId) {
        return jpaReadingSessionRepository.findAllByReadingAttemptId(attemptId).stream()
            .map(readingSessionDataMapper::toDomain).toList();
    }

    @Override
    public ReadingSession save(ReadingSession readingSession) {
        ReadingSessionEntity entity = readingSessionDataMapper.toEntity(readingSession);
        ReadingSessionEntity savedEntity = jpaReadingSessionRepository.save(entity);
        return readingSessionDataMapper.toDomain(savedEntity);
    }
}
