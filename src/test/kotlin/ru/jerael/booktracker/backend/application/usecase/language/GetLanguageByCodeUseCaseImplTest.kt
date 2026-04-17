package ru.jerael.booktracker.backend.application.usecase.language

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.repository.LanguageRepository
import ru.jerael.booktracker.backend.factory.language.LanguageDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class GetLanguageByCodeUseCaseImplTest {
    
    @MockK
    private lateinit var languageRepository: LanguageRepository
    
    @InjectMockKs
    private lateinit var useCase: GetLanguageByCodeUseCaseImpl
    
    @Test
    fun `when language exists, execute should return language`() {
        val languageCode = "en"
        val language = LanguageDomainFactory.createLanguage(code = languageCode)
        
        every { languageRepository.findByCode(languageCode) } returns Optional.of(language)
        
        val result = useCase.execute(languageCode)
        
        assertEquals(language, result)
        
        verify { languageRepository.findByCode(languageCode) }
    }
    
    @Test
    fun `when language does not exists, execute should throw NotFoundException`() {
        val languageCode = "22"
        
        every { languageRepository.findByCode(languageCode) } returns Optional.empty()
        
        assertThrows(NotFoundException::class.java) { useCase.execute(languageCode) }
    }
}