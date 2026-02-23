package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import java.util.List;

@Repository
public interface JpaGenreRepository extends JpaRepository<GenreEntity, Integer> {
    List<GenreEntity> findAllByOrderByNameAsc();
}
