package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaBookRepository extends JpaRepository<BookEntity, UUID> {
    Page<BookEntity> findAllByUserId(Pageable pageable, UUID userId);

    Optional<BookEntity> findByIdAndUserId(UUID id, UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);
}
