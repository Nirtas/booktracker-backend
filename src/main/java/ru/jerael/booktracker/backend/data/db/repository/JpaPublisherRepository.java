package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jerael.booktracker.backend.data.db.entity.PublisherEntity;
import java.util.Optional;
import java.util.UUID;

public interface JpaPublisherRepository extends JpaRepository<PublisherEntity, UUID> {
    Optional<PublisherEntity> findByNameIgnoreCase(String name);
}
