package ru.jerael.booktracker.backend.factory.language

import ru.jerael.booktracker.backend.data.db.entity.LanguageEntity

object LanguageEntityFactory {
    fun createLanguageEntity(
        code: String = "en",
        name: String = "English"
    ): LanguageEntity {
        return LanguageEntity().apply { this.code = code; this.name = name }
    }
}