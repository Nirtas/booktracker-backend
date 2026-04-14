package ru.jerael.booktracker.backend.web.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.language.LanguageDomainFactory

class LanguageWebMapperTest {
    private val mapper = LanguageWebMapper()
    
    @Test
    fun `toResponse should map Language to LanguageResponse`() {
        val language = LanguageDomainFactory.createLanguage()
        
        val response = mapper.toResponse(language)
        
        with(response) {
            assertEquals(language.code, code)
            assertEquals(language.name, name)
        }
    }
    
    @Test
    fun `toResponses should map Languages to LanguageResponses`() {
        val language1 = LanguageDomainFactory.createLanguage("en", "English")
        val language2 = LanguageDomainFactory.createLanguage("ru", "Русский")
        val languages = listOf(language1, language2)
        
        val responses = mapper.toResponses(languages)
        
        with(responses) {
            assertEquals(languages.size, size)
            
            assertEquals(language1.code, this[0].code)
            assertEquals(language1.name, this[0].name)
            
            assertEquals(language2.code, this[1].code)
            assertEquals(language2.name, this[1].name)
        }
    }
}