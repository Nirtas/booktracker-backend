package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.AuthorEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface JpaAuthorRepository extends JpaRepository<AuthorEntity, UUID> {
    Optional<AuthorEntity> findByFullNameIgnoreCase(String fullName);

    List<AuthorEntity> findAllByFullNameInIgnoreCase(Set<String> names);

    @Query(
        value = """
            SELECT *, similarity(full_name, :query) AS rank
            FROM authors
            WHERE full_name % :query OR full_name ILIKE CONCAT('%', :query, '%')
            ORDER BY rank DESC
            """,
        nativeQuery = true
    )
    List<AuthorEntity> searchBySimilarity(@Param("query") String query, Pageable pageable);
}
