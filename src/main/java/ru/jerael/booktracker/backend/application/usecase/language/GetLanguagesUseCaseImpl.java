package ru.jerael.booktracker.backend.application.usecase.language;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.repository.LanguageRepository;
import ru.jerael.booktracker.backend.domain.usecase.language.GetLanguagesUseCase;
import java.util.List;

@UseCase
@RequiredArgsConstructor
public class GetLanguagesUseCaseImpl implements GetLanguagesUseCase {
    private final LanguageRepository languageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Language> execute() {
        return languageRepository.findAll();
    }
}
