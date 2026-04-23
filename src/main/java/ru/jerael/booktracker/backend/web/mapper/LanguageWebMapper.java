package ru.jerael.booktracker.backend.web.mapper;

import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.web.annotation.WebMapper;
import ru.jerael.booktracker.backend.web.dto.language.LanguageResponse;
import java.util.List;

@WebMapper
public class LanguageWebMapper {
    public LanguageResponse toResponse(Language language) {
        if (language == null) return null;

        return new LanguageResponse(
            language.code(),
            language.name()
        );
    }

    public List<LanguageResponse> toResponses(List<Language> languages) {
        if (languages == null) return List.of();

        return languages.stream().map(this::toResponse).toList();
    }
}
