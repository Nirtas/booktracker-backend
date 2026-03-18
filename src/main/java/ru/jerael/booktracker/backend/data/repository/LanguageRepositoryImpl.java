package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.repository.JpaLanguageRepository;
import ru.jerael.booktracker.backend.data.mapper.LanguageDataMapper;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.repository.LanguageRepository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LanguageRepositoryImpl implements LanguageRepository {
    private final JpaLanguageRepository jpaLanguageRepository;
    private final LanguageDataMapper languageDataMapper;

    @Override
    public List<Language> findAll() {
        return jpaLanguageRepository.findAll().stream().map(languageDataMapper::toDomain).toList();
    }

    @Override
    public Optional<Language> findByCode(String code) {
        return jpaLanguageRepository.findByCode(code).map(languageDataMapper::toDomain);
    }
}
