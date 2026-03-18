package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.language.Language;
import java.util.List;
import java.util.Optional;

public interface LanguageRepository {
    List<Language> findAll();

    Optional<Language> findByCode(String code);
}
