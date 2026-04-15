package ru.jerael.booktracker.backend.web.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import ru.jerael.booktracker.backend.domain.exception.factory.UserExceptionFactory
import ru.jerael.booktracker.backend.domain.usecase.auth.*
import ru.jerael.booktracker.backend.domain.usecase.user.CreateUserUseCase
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory
import ru.jerael.booktracker.backend.factory.auth.AuthWebFactory
import ru.jerael.booktracker.backend.factory.user.UserDomainFactory
import ru.jerael.booktracker.backend.factory.user.UserWebFactory
import ru.jerael.booktracker.backend.web.config.WebProperties
import ru.jerael.booktracker.backend.web.dto.auth.AuthResponse
import ru.jerael.booktracker.backend.web.dto.auth.ResendVerificationResponse
import ru.jerael.booktracker.backend.web.dto.user.UserCreationResponse
import ru.jerael.booktracker.backend.web.mapper.AuthWebMapper
import ru.jerael.booktracker.backend.web.mapper.UserWebMapper
import tools.jackson.databind.ObjectMapper

@WebMvcTest(AuthController::class)
@Import(UserWebMapper::class, AuthWebMapper::class)
class AuthControllerTest {
    
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester
    
    @MockkBean
    private lateinit var webProperties: WebProperties
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean
    private lateinit var createUserUseCase: CreateUserUseCase
    
    @MockkBean
    private lateinit var confirmRegistrationUseCase: ConfirmRegistrationUseCase
    
    @MockkBean
    private lateinit var loginUserUseCase: LoginUserUseCase
    
    @MockkBean
    private lateinit var refreshTokensUseCase: RefreshTokensUseCase
    
    @MockkBean
    private lateinit var logoutUserUseCase: LogoutUserUseCase
    
    @MockkBean
    private lateinit var resendVerificationUseCase: ResendVerificationUseCase
    
    @Test
    fun `register should return created User`() {
        val request = UserWebFactory.createUserCreationRequest()
        val result = UserDomainFactory.createUserCreationResult()
        every { createUserUseCase.execute(any()) } returns result
        
        assertThat(
            mockMvcTester.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.CREATED)
            .bodyJson()
            .convertTo(UserCreationResponse::class.java)
            .satisfies({ response ->
                assertEquals(result.userId, response.userId)
                assertEquals(result.email, response.email)
                assertEquals(result.expiresAt, response.expiresAt)
            })
    }
    
    @Test
    fun `when email already exists, register should return 409 CONFLICT`() {
        val existingEmail = "existing@example.com"
        val request = UserWebFactory.createUserCreationRequest(email = existingEmail)
        every { createUserUseCase.execute(any()) }
            .throws(UserExceptionFactory.emailAlreadyExists(existingEmail))
        
        assertThat(
            mockMvcTester.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.CONFLICT)
            .bodyJson()
            .extractingPath("$.detail").asString().contains(existingEmail)
    }
    
    @Test
    fun `when request is valid, confirmRegistration should return TokenPair`() {
        val request = AuthWebFactory.createConfirmRegistrationRequest()
        val tokenPair = AuthDomainFactory.createTokenPair()
        every { confirmRegistrationUseCase.execute(any()) } returns tokenPair
        
        assertThat(
            mockMvcTester.post().uri("/api/v1/auth/confirm-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(AuthResponse::class.java)
            .satisfies({ response ->
                assertEquals(tokenPair.accessToken, response.accessToken)
                assertEquals(tokenPair.refreshToken, response.refreshToken)
            })
    }
    
    @Test
    fun `when request is valid, login should return TokenPair`() {
        val request = AuthWebFactory.createLoginRequest()
        val tokenPair = AuthDomainFactory.createTokenPair()
        every { loginUserUseCase.execute(any()) } returns tokenPair
        
        assertThat(
            mockMvcTester.post().uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(AuthResponse::class.java)
            .satisfies({ response ->
                assertEquals(tokenPair.accessToken, response.accessToken)
                assertEquals(tokenPair.refreshToken, response.refreshToken)
            })
    }
    
    @Test
    fun `when request is valid, refresh should return TokenPair`() {
        val request = AuthWebFactory.createRefreshTokensRequest()
        val tokenPair = AuthDomainFactory.createTokenPair()
        every { refreshTokensUseCase.execute(any()) } returns tokenPair
        
        assertThat(
            mockMvcTester.post().uri("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(AuthResponse::class.java)
            .satisfies({ response ->
                assertEquals(tokenPair.accessToken, response.accessToken)
                assertEquals(tokenPair.refreshToken, response.refreshToken)
            })
    }
    
    @Test
    fun `when request is valid, logout should return 200 OK`() {
        val request = AuthWebFactory.createLogoutRequest()
        every { logoutUserUseCase.execute(any()) } just Runs
        
        assertThat(
            mockMvcTester.post().uri("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.OK)
    }
    
    @Test
    fun `when request is valid, resendCode should return ResendVerificationResponse`() {
        val request = AuthWebFactory.createResendVerificationRequest()
        val result = AuthDomainFactory.createResendVerificationResult()
        every { resendVerificationUseCase.execute(any()) } returns result
        
        assertThat(
            mockMvcTester.post().uri("/api/v1/auth/resend-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatus(HttpStatus.OK)
            .bodyJson()
            .convertTo(ResendVerificationResponse::class.java)
            .satisfies({ response ->
                assertEquals(result.userId, response.userId)
                assertEquals(result.email, response.email)
                assertEquals(result.expiresAt, response.expiresAt)
            })
    }
}