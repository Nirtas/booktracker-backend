package ru.jerael.booktracker.backend.application.usecase.language;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.exception.factory.LanguageExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.repository.LanguageRepository;
import ru.jerael.booktracker.backend.domain.usecase.language.GetLanguageByCodeUseCase;

@UseCase
@RequiredArgsConstructor
public class GetLanguageByCodeUseCaseImpl implements GetLanguageByCodeUseCase {
    private final LanguageRepository languageRepository;

    @Override
    @Transactional(readOnly = true)
    public Language execute(String code) {
        return languageRepository.findByCode(code).orElseThrow(() -> LanguageExceptionFactory.languageNotFound(code));
    }
}
