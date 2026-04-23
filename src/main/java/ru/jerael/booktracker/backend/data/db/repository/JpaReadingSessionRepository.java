package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.ReadingSessionEntity;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaReadingSessionRepository extends JpaRepository<ReadingSessionEntity, UUID> {
    List<ReadingSessionEntity> findAllByReadingAttemptId(UUID attemptId);
}
