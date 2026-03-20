package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.code.LanguageErrorCode;

public class LanguageExceptionFactory {
    public static NotFoundException languageNotFound(String languageCode) {
        return new NotFoundException(
            LanguageErrorCode.LANGUAGE_NOT_FOUND,
            "Language with code '" + languageCode + "' not found"
        );
    }
}
