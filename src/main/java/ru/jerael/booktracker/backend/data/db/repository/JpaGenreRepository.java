package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import java.util.List;
import java.util.Set;

@Repository
public interface JpaGenreRepository extends JpaRepository<GenreEntity, Integer> {
    List<GenreEntity> findAllByOrderByNameAsc();

    List<GenreEntity> findAllByNameInIgnoreCase(Set<String> names);
}
