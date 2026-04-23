package ru.jerael.booktracker.backend.domain.usecase.language;

import ru.jerael.booktracker.backend.domain.model.language.Language;

public interface GetLanguageByCodeUseCase {
    Language execute(String code);
}
