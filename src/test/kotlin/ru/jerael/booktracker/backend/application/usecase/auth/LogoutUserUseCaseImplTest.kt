package ru.jerael.booktracker.backend.application.usecase.auth

import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.domain.exception.factory.IdentityTokenExceptionFactory
import ru.jerael.booktracker.backend.domain.service.token.AuthTokenService
import ru.jerael.booktracker.backend.domain.validator.AuthValidator
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class LogoutUserUseCaseImplTest {
    
    @MockK(relaxed = true)
    private lateinit var authValidator: AuthValidator
    
    @MockK(relaxed = true)
    private lateinit var authTokenService: AuthTokenService
    
    @InjectMockKs
    private lateinit var useCase: LogoutUserUseCaseImpl
    
    @Test
    fun `when payload is valid, execute should revoke old`() {
        val refreshToken = "refresh token"
        val userId = UUID.randomUUID()
        val data = AuthDomainFactory.createLogoutPayload(refreshToken = refreshToken)
        
        every { authTokenService.revokeToken(refreshToken) } returns userId
        
        useCase.execute(data)
        
        verify { authValidator.validateLogoutPayload(data) }
        verify { authTokenService.revokeToken(refreshToken) }
    }
    
    @Test
    fun `when validation fails, execute should throw ValidationException`() {
        val data = AuthDomainFactory.createLogoutPayload()
        
        every { authValidator.validateLogoutPayload(data) } throws ValidationException(listOf())
        
        assertThrows(ValidationException::class.java) { useCase.execute(data) }
        
        verify { authTokenService wasNot called }
    }
    
    @Test
    fun `when revoke fails, execute should throw UnauthenticatedException`() {
        val data = AuthDomainFactory.createLogoutPayload()
        
        every { authTokenService.revokeToken(data.refreshToken) } throws IdentityTokenExceptionFactory.invalidToken()
        
        assertThrows(UnauthenticatedException::class.java) { useCase.execute(data) }
    }
}