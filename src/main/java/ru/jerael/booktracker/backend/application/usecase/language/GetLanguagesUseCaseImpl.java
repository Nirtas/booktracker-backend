package ru.jerael.booktracker.backend.application.usecase.language;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.repository.LanguageRepository;
import ru.jerael.booktracker.backend.domain.usecase.language.GetLanguagesUseCase;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetLanguagesUseCaseImpl implements GetLanguagesUseCase {
    private final LanguageRepository languageRepository;

    @Override
    public List<Language> execute() {
        return languageRepository.findAll();
    }
}
