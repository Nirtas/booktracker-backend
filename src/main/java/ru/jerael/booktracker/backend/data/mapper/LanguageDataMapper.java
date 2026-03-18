package ru.jerael.booktracker.backend.data.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.LanguageEntity;
import ru.jerael.booktracker.backend.domain.model.language.Language;

@Component
public class LanguageDataMapper {
    public Language toDomain(LanguageEntity entity) {
        if (entity == null) return null;

        return new Language(
            entity.getCode(),
            entity.getName()
        );
    }
}
