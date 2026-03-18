package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.entity.LanguageEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaLanguageRepository;
import ru.jerael.booktracker.backend.data.mapper.LanguageDataMapper;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({LanguageRepositoryImpl.class, LanguageDataMapper.class})
class LanguageRepositoryImplTest {

    @Autowired
    private LanguageRepositoryImpl languageRepository;

    @Autowired
    private JpaLanguageRepository jpaLanguageRepository;

    @Test
    void findAll_ShouldReturnListOfLanguages() {
        LanguageEntity entity1 = new LanguageEntity();
        entity1.setCode("en");
        entity1.setName("English");
        LanguageEntity entity2 = new LanguageEntity();
        entity2.setCode("ru");
        entity2.setName("Русский");
        jpaLanguageRepository.save(entity1);
        jpaLanguageRepository.save(entity2);

        List<Language> languages = languageRepository.findAll();

        assertEquals(2, languages.size());
        assertEquals("en", languages.get(0).code());
        assertEquals("ru", languages.get(1).code());
    }

    @Test
    void findByCode_WhenExists_ShouldReturnLanguage() {
        LanguageEntity entity1 = new LanguageEntity();
        entity1.setCode("en");
        entity1.setName("English");
        jpaLanguageRepository.save(entity1);

        Optional<Language> language = languageRepository.findByCode("en");

        assertTrue(language.isPresent());
        assertEquals("en", language.get().code());
        assertEquals("English", language.get().name());
    }
}