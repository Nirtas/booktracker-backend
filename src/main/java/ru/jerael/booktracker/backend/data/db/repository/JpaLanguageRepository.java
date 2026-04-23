package ru.jerael.booktracker.backend.data.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.LanguageEntity;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaLanguageRepository extends JpaRepository<LanguageEntity, String> {
    Optional<LanguageEntity> findByCode(String code);

    List<LanguageEntity> findAllByOrderByNameAsc();
}
