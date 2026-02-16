package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import java.util.UUID;

@Repository
public interface JpaBookRepository extends JpaRepository<BookEntity, UUID> {}
