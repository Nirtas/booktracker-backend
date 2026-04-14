package ru.jerael.booktracker.backend.factory.language

import ru.jerael.booktracker.backend.web.dto.language.LanguageResponse

object LanguageWebFactory {
    fun createLanguageResponse(
        code: String = "en",
        name: String = "English"
    ): LanguageResponse {
        return LanguageResponse(code, name)
    }
}