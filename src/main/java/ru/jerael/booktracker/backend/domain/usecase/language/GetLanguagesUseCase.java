package ru.jerael.booktracker.backend.domain.usecase.language;

import ru.jerael.booktracker.backend.domain.model.language.Language;
import java.util.List;

public interface GetLanguagesUseCase {
    List<Language> execute();
}
