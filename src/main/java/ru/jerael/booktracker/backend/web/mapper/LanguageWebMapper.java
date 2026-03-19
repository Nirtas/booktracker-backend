package ru.jerael.booktracker.backend.web.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.web.dto.language.LanguageResponse;

@Component
public class LanguageWebMapper {
    public LanguageResponse toResponse(Language language) {
        if (language == null) return null;

        return new LanguageResponse(
            language.code(),
            language.name()
        );
    }
}
