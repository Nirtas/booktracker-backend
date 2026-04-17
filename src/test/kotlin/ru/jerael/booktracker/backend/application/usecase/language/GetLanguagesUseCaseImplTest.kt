package ru.jerael.booktracker.backend.application.usecase.language

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.model.language.Language
import ru.jerael.booktracker.backend.domain.repository.LanguageRepository

@ExtendWith(MockKExtension::class)
class GetLanguagesUseCaseImplTest {
    
    @MockK
    private lateinit var languageRepository: LanguageRepository
    
    @InjectMockKs
    private lateinit var useCase: GetLanguagesUseCaseImpl
    
    @Test
    fun `execute should return list of languages`() {
        val languages = listOf(
            Language("en", "English"),
            Language("ru", "Русский")
        )
        
        every { languageRepository.findAll() } returns languages
        
        with(useCase.execute()) {
            assertEquals(languages.size, size)
            assertEquals(languages, this)
        }
    }
}