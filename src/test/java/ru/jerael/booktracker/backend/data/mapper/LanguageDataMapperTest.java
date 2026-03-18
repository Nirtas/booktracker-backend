package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.LanguageEntity;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageDataMapperTest {
    private final LanguageDataMapper languageDataMapper = new LanguageDataMapper();

    private final String code = "en";
    private final String name = "English";

    @Test
    void toDomain() {
        LanguageEntity entity = new LanguageEntity();
        entity.setCode(code);
        entity.setName(name);

        Language result = languageDataMapper.toDomain(entity);

        assertEquals(code, result.code());
        assertEquals(name, result.name());
    }
}