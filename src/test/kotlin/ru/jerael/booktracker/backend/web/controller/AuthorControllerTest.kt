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
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService
import ru.jerael.booktracker.backend.domain.usecase.author.SearchAuthorsUseCase
import ru.jerael.booktracker.backend.factory.auth.AuthWebFactory
import ru.jerael.booktracker.backend.factory.author.AuthorDomainFactory
import ru.jerael.booktracker.backend.factory.author.AuthorWebFactory
import ru.jerael.booktracker.backend.web.config.RateLimitProperties
import ru.jerael.booktracker.backend.web.config.WebProperties
import ru.jerael.booktracker.backend.web.dto.author.AuthorResponse
import ru.jerael.booktracker.backend.web.exception.handler.GlobalExceptionHandler
import ru.jerael.booktracker.backend.web.mapper.AuthorWebMapper
import ru.jerael.booktracker.backend.web.security.SecurityConfig

@WebMvcTest(AuthorController::class)
@Import(GlobalExceptionHandler::class, SecurityConfig::class)
class AuthorControllerTest {
    
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester
    
    @MockkBean
    private lateinit var webProperties: WebProperties
    
    @MockkBean
    private lateinit var rateLimitProperties: RateLimitProperties
    
    @MockkBean
    private lateinit var authTokenService: AuthTokenService
    
    @MockkBean
    private lateinit var searchAuthorsUseCase: SearchAuthorsUseCase
    
    @MockkBean
    private lateinit var authorWebMapper: AuthorWebMapper
    
    @Test
    fun `autocomplete should return list of authors`() {
        val query = "hor"
        val authors = listOf(AuthorDomainFactory.createAuthor(fullName = "Author A"))
        val authorResponses = listOf(AuthorWebFactory.createAuthorResponse(fullName = "Author A"))
        val pageResult = PageResult(authors, 1, 0, 1, 1)
        
        every { searchAuthorsUseCase.execute(any(), query) } returns pageResult
        every { authorWebMapper.toResponses(pageResult) } returns authorResponses
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/authors/autocomplete")
            .queryParam("query", query)
            .queryParam("limit", "5")
            .with(authentication(AuthWebFactory.createAuthToken()))
            .exchange()
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(Array<AuthorResponse>::class.java)
            .satisfies({ responses ->
                assertThat(responses).hasSize(1)
                assertThat(responses[0].fullName).isEqualTo("Author A")
            })
        
        verify { searchAuthorsUseCase.execute(any(), query) }
    }
}