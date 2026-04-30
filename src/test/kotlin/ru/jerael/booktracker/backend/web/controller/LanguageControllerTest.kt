package ru.jerael.booktracker.backend.web.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.web.servlet.assertj.MockMvcTester
import ru.jerael.booktracker.backend.domain.exception.factory.LanguageExceptionFactory
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService
import ru.jerael.booktracker.backend.domain.usecase.language.GetLanguageByCodeUseCase
import ru.jerael.booktracker.backend.domain.usecase.language.GetLanguagesUseCase
import ru.jerael.booktracker.backend.factory.auth.AuthWebFactory
import ru.jerael.booktracker.backend.factory.language.LanguageDomainFactory
import ru.jerael.booktracker.backend.factory.language.LanguageWebFactory
import ru.jerael.booktracker.backend.web.config.RateLimitProperties
import ru.jerael.booktracker.backend.web.config.WebProperties
import ru.jerael.booktracker.backend.web.dto.language.LanguageResponse
import ru.jerael.booktracker.backend.web.exception.handler.GlobalExceptionHandler
import ru.jerael.booktracker.backend.web.mapper.LanguageWebMapper
import ru.jerael.booktracker.backend.web.security.SecurityConfig

@WebMvcTest(LanguageController::class)
@Import(GlobalExceptionHandler::class, LanguageWebMapper::class, SecurityConfig::class)
class LanguageControllerTest {
    
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester
    
    @MockkBean
    private lateinit var webProperties: WebProperties
    
    @MockkBean
    private lateinit var rateLimitProperties: RateLimitProperties
    
    @MockkBean
    private lateinit var authTokenService: AuthTokenService
    
    @MockkBean
    private lateinit var getLanguagesUseCase: GetLanguagesUseCase
    
    @MockkBean
    private lateinit var getLanguageByCodeUseCase: GetLanguageByCodeUseCase
    
    @Test
    fun `getAll should return list of Languages`() {
        val language = LanguageDomainFactory.createLanguage()
        val languageResponse = LanguageWebFactory.createLanguageResponse()
        
        every { getLanguagesUseCase.execute() } returns listOf(language)
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/languages")
            .with(authentication(AuthWebFactory.createAuthToken()))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(Array<LanguageResponse>::class.java)
            .satisfies({ responses ->
                assertThat(responses).containsExactly(languageResponse)
            })
        
        verify { getLanguagesUseCase.execute() }
    }
    
    @Test
    fun `when language not found, getByCode should return 404 NOT FOUND`() {
        val languageCode = "22"
        
        every { getLanguageByCodeUseCase.execute(languageCode) }
            .throws(LanguageExceptionFactory.languageNotFound(languageCode))
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/languages/$languageCode")
            .with(authentication(AuthWebFactory.createAuthToken()))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.NOT_FOUND)
            .bodyJson()
            .extractingPath("$.detail")
            .isEqualTo("Language with code '$languageCode' not found")
        
        verify { getLanguageByCodeUseCase.execute(languageCode) }
    }
}