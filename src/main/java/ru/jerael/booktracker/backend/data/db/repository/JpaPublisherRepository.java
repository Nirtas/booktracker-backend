package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.PublisherEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPublisherRepository extends JpaRepository<PublisherEntity, UUID> {
    Optional<PublisherEntity> findByNameIgnoreCase(String name);

    @Query(
        value = """
            SELECT *, similarity(name, :query) AS rank
            FROM publishers
            WHERE name % :query OR name ILIKE CONCAT('%', :query, '%')
            ORDER BY rank DESC
            """,
        nativeQuery = true
    )
    List<PublisherEntity> searchBySimilarity(@Param("query") String query, Pageable pageable);
}
