package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.NoteEntity;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaNoteRepository extends JpaRepository<NoteEntity, UUID> {
    List<NoteEntity> findAllByBookId(UUID bookId);
}
