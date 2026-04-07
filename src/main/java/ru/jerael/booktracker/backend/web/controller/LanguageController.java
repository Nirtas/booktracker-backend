package ru.jerael.booktracker.backend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.usecase.language.GetLanguagesUseCase;
import ru.jerael.booktracker.backend.web.dto.language.LanguageResponse;
import ru.jerael.booktracker.backend.web.mapper.LanguageWebMapper;
import java.util.List;

@Tag(name = "Languages")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/languages")
public class LanguageController {
    private final GetLanguagesUseCase getLanguagesUseCase;
    private final LanguageWebMapper languageWebMapper;

    @Operation(summary = "Get all languages")
    @GetMapping
    public List<LanguageResponse> getAll() {
        List<Language> languages = getLanguagesUseCase.execute();
        return languageWebMapper.toResponses(languages);
    }
}
