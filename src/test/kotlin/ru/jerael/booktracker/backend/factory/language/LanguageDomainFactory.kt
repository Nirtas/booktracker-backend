package ru.jerael.booktracker.backend.factory.language

import ru.jerael.booktracker.backend.domain.model.language.Language

object LanguageDomainFactory {
    fun createLanguage(
        code: String = "en",
        name: String = "English"
    ): Language {
        return Language(code, name)
    }
}