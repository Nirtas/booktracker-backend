package ru.jerael.booktracker.backend.application.usecase.auth

import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
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
class RefreshTokensUseCaseImplTest {
    
    @MockK(relaxed = true)
    private lateinit var authValidator: AuthValidator
    
    @MockK
    private lateinit var authTokenService: AuthTokenService
    
    @InjectMockKs
    private lateinit var useCase: RefreshTokensUseCaseImpl
    
    @Test
    fun `execute should revoke old and return new TokenPair`() {
        val data = AuthDomainFactory.createRefreshTokenPayload()
        val tokenPair = AuthDomainFactory.createTokenPair()
        val userId = UUID.randomUUID()
        
        every { authTokenService.revokeToken(data.refreshToken) } returns userId
        every { authTokenService.issueTokens(userId) } returns tokenPair
        
        val result = useCase.execute(data)
        
        assertEquals(tokenPair, result)
        
        verify { authValidator.validateRefreshTokenPayload(data) }
        verify { authTokenService.revokeToken(data.refreshToken) }
        verify { authTokenService.issueTokens(userId) }
    }
    
    @Test
    fun `when validation fails, execute should throw ValidationException`() {
        val data = AuthDomainFactory.createRefreshTokenPayload()
        
        every { authValidator.validateRefreshTokenPayload(data) } throws ValidationException(listOf())
        
        assertThrows(ValidationException::class.java) { useCase.execute(data) }
        
        verify { authTokenService wasNot called }
    }
    
    @Test
    fun `when revoke fails, execute should throw UnauthenticatedException`() {
        val data = AuthDomainFactory.createRefreshTokenPayload()
        
        every { authTokenService.revokeToken(data.refreshToken) } throws IdentityTokenExceptionFactory.invalidToken()
        
        assertThrows(UnauthenticatedException::class.java) { useCase.execute(data) }
        
        verify(exactly = 0) { authTokenService.issueTokens(any()) }
    }
}