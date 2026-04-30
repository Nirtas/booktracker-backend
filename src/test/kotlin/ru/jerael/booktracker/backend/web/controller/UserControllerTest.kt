package ru.jerael.booktracker.backend.web.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.web.servlet.assertj.MockMvcTester
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService
import ru.jerael.booktracker.backend.domain.usecase.user.GetUserUseCase
import ru.jerael.booktracker.backend.factory.auth.AuthWebFactory
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory
import ru.jerael.booktracker.backend.web.config.RateLimitProperties
import ru.jerael.booktracker.backend.web.config.WebProperties
import ru.jerael.booktracker.backend.web.exception.handler.GlobalExceptionHandler
import ru.jerael.booktracker.backend.web.mapper.UserWebMapper
import ru.jerael.booktracker.backend.web.security.SecurityConfig

@WebMvcTest(UserController::class)
@Import(GlobalExceptionHandler::class, UserWebMapper::class, SecurityConfig::class)
class UserControllerTest {
    
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester
    
    @MockkBean
    private lateinit var webProperties: WebProperties
    
    @MockkBean
    private lateinit var rateLimitProperties: RateLimitProperties
    
    @MockkBean
    private lateinit var authTokenService: AuthTokenService
    
    @MockkBean
    private lateinit var getUserUseCase: GetUserUseCase
    
    @Test
    fun `when authenticated, getMe should return UserResponse`() {
        val user = UserDomainFactory.createUser()
        
        every { getUserUseCase.execute(user.id) } returns user
        
        val mockResponse = mockMvcTester.get().uri("/api/v1/users/me")
            .with(authentication(AuthWebFactory.createAuthToken(userId = user.id)))
        
        assertThat(mockResponse)
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .extractingPath("$.email")
            .isEqualTo(user.email)
    }
}