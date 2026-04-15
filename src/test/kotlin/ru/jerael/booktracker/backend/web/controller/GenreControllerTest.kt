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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.web.servlet.assertj.MockMvcTester
import ru.jerael.booktracker.backend.domain.exception.factory.GenreExceptionFactory
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenreByIdUseCase
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenresUseCase
import ru.jerael.booktracker.backend.factory.genre.GenreDomainFactory
import ru.jerael.booktracker.backend.factory.genre.GenreWebFactory
import ru.jerael.booktracker.backend.web.config.WebProperties
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse
import ru.jerael.booktracker.backend.web.exception.handler.GlobalExceptionHandler
import ru.jerael.booktracker.backend.web.mapper.GenreWebMapper
import ru.jerael.booktracker.backend.web.security.SecurityConfig
import java.util.*

@WebMvcTest(GenreController::class)
@Import(GlobalExceptionHandler::class, GenreWebMapper::class, SecurityConfig::class)
class GenreControllerTest {
    
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester
    
    @MockkBean
    private lateinit var webProperties: WebProperties
    
    @MockkBean
    private lateinit var getGenresUseCase: GetGenresUseCase
    
    @MockkBean
    private lateinit var getGenreByIdUseCase: GetGenreByIdUseCase
    
    @MockkBean
    private lateinit var authTokenService: AuthTokenService
    
    private fun getAuth(): UsernamePasswordAuthenticationToken {
        return UsernamePasswordAuthenticationToken(UUID.randomUUID(), null, emptyList())
    }
    
    @Test
    fun `getAll should return set of Genres`() {
        val genre = GenreDomainFactory.createGenre()
        val genreResponse = GenreWebFactory.createGenreResponse()
        
        every { getGenresUseCase.execute() } returns setOf(genre)
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/genres")
            .with(authentication(getAuth()))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(Array<GenreResponse>::class.java)
            .satisfies({ responses ->
                assertThat(responses).containsExactly(genreResponse)
            })
        
        verify { getGenresUseCase.execute() }
    }
    
    @Test
    fun `when genre not found, getById should return 404 NOT FOUND`() {
        val genreId = 5555
        
        every { getGenreByIdUseCase.execute(genreId) } throws GenreExceptionFactory.genreNotFound(genreId)
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/genres/$genreId")
            .with(authentication(getAuth()))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.NOT_FOUND)
            .bodyJson()
            .extractingPath("$.detail")
            .isEqualTo("Genre not found with id: $genreId")
        
        verify { getGenreByIdUseCase.execute(genreId) }
    }
}