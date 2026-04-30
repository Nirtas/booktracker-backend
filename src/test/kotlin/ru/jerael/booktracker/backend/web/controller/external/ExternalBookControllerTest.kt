package ru.jerael.booktracker.backend.web.controller.external

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
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService
import ru.jerael.booktracker.backend.domain.usecase.external.book.GetBookMetadataUseCase
import ru.jerael.booktracker.backend.factory.auth.AuthWebFactory
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import ru.jerael.booktracker.backend.factory.book.BookWebFactory
import ru.jerael.booktracker.backend.web.config.WebProperties
import ru.jerael.booktracker.backend.web.dto.book.BookMetadataResponse
import ru.jerael.booktracker.backend.web.exception.handler.GlobalExceptionHandler
import ru.jerael.booktracker.backend.web.mapper.BookWebMapper
import ru.jerael.booktracker.backend.web.security.SecurityConfig

@WebMvcTest(ExternalBookController::class)
@Import(GlobalExceptionHandler::class, SecurityConfig::class)
class ExternalBookControllerTest {
    
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester
    
    @MockkBean
    private lateinit var webProperties: WebProperties
    
    @MockkBean
    private lateinit var authTokenService: AuthTokenService
    
    @MockkBean
    private lateinit var getBookMetadataUseCase: GetBookMetadataUseCase
    
    @MockkBean
    private lateinit var bookWebMapper: BookWebMapper
    
    @Test
    fun `findBookMetadata should return 200 OK and book metadata`() {
        val isbn = "1234567890"
        val metadata = BookDomainFactory.createBookMetadata(isbn10 = isbn)
        val response = BookWebFactory.createBookMetadataResponse(isbn10 = isbn)
        
        every { getBookMetadataUseCase.execute(any()) } returns metadata
        every { bookWebMapper.toResponse(metadata) } returns response
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/external/books")
            .queryParam("isbn", isbn)
            .with(authentication(AuthWebFactory.createAuthToken()))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(BookMetadataResponse::class.java)
            .satisfies({ response ->
                assertThat(response.isbn10).isEqualTo(isbn)
            })
        
        verify { getBookMetadataUseCase.execute(any()) }
    }
    
    @Test
    fun `when book metadata not found, findBookMetadata should return 404 NOT FOUND`() {
        val isbn = "1234567890"
        
        every { getBookMetadataUseCase.execute(any()) } throws BookExceptionFactory.bookMetadataNotFound()
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/external/books")
            .queryParam("isbn", isbn)
            .with(authentication(AuthWebFactory.createAuthToken()))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.NOT_FOUND)
            .bodyJson()
            .extractingPath("$.detail")
            .isEqualTo("Book metadata not found")
        
        verify { getBookMetadataUseCase.execute(any()) }
    }
}