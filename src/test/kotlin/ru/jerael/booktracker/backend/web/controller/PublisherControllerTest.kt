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
import ru.jerael.booktracker.backend.domain.usecase.publisher.SearchPublishersUseCase
import ru.jerael.booktracker.backend.factory.auth.AuthWebFactory
import ru.jerael.booktracker.backend.factory.publisher.PublisherDomainFactory
import ru.jerael.booktracker.backend.factory.publisher.PublisherWebFactory
import ru.jerael.booktracker.backend.web.config.RateLimitProperties
import ru.jerael.booktracker.backend.web.config.WebProperties
import ru.jerael.booktracker.backend.web.dto.publisher.PublisherResponse
import ru.jerael.booktracker.backend.web.exception.handler.GlobalExceptionHandler
import ru.jerael.booktracker.backend.web.mapper.PublisherWebMapper
import ru.jerael.booktracker.backend.web.security.SecurityConfig

@WebMvcTest(PublisherController::class)
@Import(GlobalExceptionHandler::class, SecurityConfig::class)
class PublisherControllerTest {
    
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester
    
    @MockkBean
    private lateinit var webProperties: WebProperties
    
    @MockkBean
    private lateinit var rateLimitProperties: RateLimitProperties
    
    @MockkBean
    private lateinit var authTokenService: AuthTokenService
    
    @MockkBean
    private lateinit var searchPublishersUseCase: SearchPublishersUseCase
    
    @MockkBean
    private lateinit var publisherWebMapper: PublisherWebMapper
    
    @Test
    fun `autocomplete should return list of publishers`() {
        val query = "she"
        val publishers = listOf(PublisherDomainFactory.createPublisher(name = "Publisher A"))
        val publisherResponses = listOf(PublisherWebFactory.createPublisherResponse(name = "Publisher A"))
        val pageResult = PageResult(publishers, 1, 0, 1, 1)
        
        every { searchPublishersUseCase.execute(any(), query) } returns pageResult
        every { publisherWebMapper.toResponses(pageResult) } returns publisherResponses
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/publishers/autocomplete")
            .queryParam("query", query)
            .queryParam("limit", "5")
            .with(authentication(AuthWebFactory.createAuthToken()))
            .exchange()
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(Array<PublisherResponse>::class.java)
            .satisfies({ responses ->
                assertThat(responses).hasSize(1)
                assertThat(responses[0].name).isEqualTo("Publisher A")
            })
        
        verify { searchPublishersUseCase.execute(any(), query) }
    }
}