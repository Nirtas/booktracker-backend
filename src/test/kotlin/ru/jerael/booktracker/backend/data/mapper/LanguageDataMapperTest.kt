package ru.jerael.booktracker.backend.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.language.LanguageDomainFactory
import ru.jerael.booktracker.backend.factory.language.LanguageEntityFactory

class LanguageDataMapperTest {
    private val mapper = LanguageDataMapper()
    
    @Test
    fun `toEntity should map Language to LanguageEntity`() {
        val language = LanguageDomainFactory.createLanguage()
        
        val entity = mapper.toEntity(language)
        
        with(entity) {
            assertEquals(language.code, code)
            assertEquals(language.name, name)
        }
    }
    
    @Test
    fun `toDomain should map LanguageEntity to Language`() {
        val entity = LanguageEntityFactory.createLanguageEntity()
        
        val language = mapper.toDomain(entity)
        
        with(language) {
            assertEquals(entity.code, code)
            assertEquals(entity.name, name)
        }
    }
}