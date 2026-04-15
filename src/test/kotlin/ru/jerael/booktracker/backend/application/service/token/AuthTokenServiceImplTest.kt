package ru.jerael.booktracker.backend.application.service.token

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.application.service.token.config.AuthTokenProperties
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException
import ru.jerael.booktracker.backend.domain.exception.code.IdentityTokenErrorCode
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenClaims
import ru.jerael.booktracker.backend.domain.model.auth.IdentityTokenType
import ru.jerael.booktracker.backend.domain.model.auth.RefreshToken
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository
import ru.jerael.booktracker.backend.domain.service.token.IdentityTokenProvider
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory
import java.time.Duration
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
class AuthTokenServiceImplTest {
    
    @MockK
    private lateinit var properties: AuthTokenProperties
    
    @MockK
    private lateinit var identityTokenProvider: IdentityTokenProvider
    
    @MockK
    private lateinit var passwordHasher: PasswordHasher
    
    @MockK
    private lateinit var refreshTokenRepository: RefreshTokenRepository
    
    @InjectMockKs
    private lateinit var service: AuthTokenServiceImpl
    
    private val userId: UUID = UUID.randomUUID()
    private val token: String = "token"
    
    @BeforeEach
    fun setUp() {
        every { properties.issuer } returns "issuer"
        every { properties.accessExpiry } returns Duration.ofMinutes(15)
        every { properties.refreshExpiry } returns Duration.ofDays(30)
    }
    
    @Test
    fun `issueTokens should save hashed refreshToken and return TokenPair`() {
        val accessToken = "access token"
        val refreshToken = "refresh token"
        val hash = "refresh token hash"
        
        every { identityTokenProvider.encode(any()) } answers {
            val claims = it.invocation.args[0] as IdentityTokenClaims
            if (claims.type == IdentityTokenType.ACCESS) accessToken else refreshToken
        }
        
        every { passwordHasher.hash(refreshToken) } returns hash
        every { refreshTokenRepository.save(any()) } just Runs
        
        val result = service.issueTokens(userId)
        
        assertEquals(accessToken, result.accessToken)
        assertEquals(refreshToken, result.refreshToken)
        
        val capturedClaims = mutableListOf<IdentityTokenClaims>()
        verify(exactly = 2) { identityTokenProvider.encode(capture(capturedClaims)) }
        
        val accessClaims = capturedClaims[0]
        assertEquals(userId, accessClaims.userId)
        assertEquals(IdentityTokenType.ACCESS, accessClaims.type)
        assertThat(accessClaims.expiresAt).isAfter(Instant.now())
        
        val refreshClaims = capturedClaims[1]
        assertEquals(userId, refreshClaims.userId)
        assertEquals(IdentityTokenType.REFRESH, refreshClaims.type)
        assertThat(refreshClaims.expiresAt).isAfter(accessClaims.expiresAt)
        
        verify { passwordHasher.hash(refreshToken) }
        
        val refreshTokenSlot = slot<RefreshToken>()
        verify { refreshTokenRepository.save(capture(refreshTokenSlot)) }
        
        val savedToken = refreshTokenSlot.captured
        assertEquals(userId, savedToken.userId)
        assertEquals(hash, savedToken.tokenHash)
        assertEquals(refreshClaims.expiresAt, savedToken.expiresAt)
    }
    
    @Test
    fun `revokeToken should delete existing refreshToken and return userId`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims(userId = userId, type = IdentityTokenType.REFRESH)
        val refreshToken = AuthDomainFactory.createRefreshToken(userId = userId)
        
        every { identityTokenProvider.decode(token) } returns claims
        every { refreshTokenRepository.findAllByUserId(userId) } returns listOf(refreshToken)
        every { passwordHasher.verify(token, refreshToken.tokenHash) } returns true
        every { refreshTokenRepository.deleteById(refreshToken.id) } just Runs
        
        val result = service.revokeToken(token)
        
        assertEquals(userId, result)
        
        verify { refreshTokenRepository.deleteById(refreshToken.id) }
    }
    
    @Test
    fun `when issuer is invalid, revokeToken should throw UnauthenticatedException`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims(issuer = "wrong issuer")
        
        every { identityTokenProvider.decode(token) } returns claims
        
        val exception = assertThrows(UnauthenticatedException::class.java) { service.revokeToken(token) }
        
        assertEquals(IdentityTokenErrorCode.INVALID_TOKEN, exception.errorCode)
        
        verify { refreshTokenRepository wasNot called }
        verify { passwordHasher wasNot called }
    }
    
    @Test
    fun `when token type is invalid, revokeToken should throw UnauthenticatedException`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims(type = IdentityTokenType.ACCESS)
        
        every { identityTokenProvider.decode(token) } returns claims
        
        val exception = assertThrows(UnauthenticatedException::class.java) { service.revokeToken(token) }
        
        assertEquals(IdentityTokenErrorCode.INVALID_TOKEN, exception.errorCode)
        
        verify { refreshTokenRepository wasNot called }
        verify { passwordHasher wasNot called }
    }
    
    @Test
    fun `when token is expired, revokeToken should throw UnauthenticatedException`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims(
            expiresAt = Instant.now().minusSeconds(1000)
        )
        
        every { identityTokenProvider.decode(token) } returns claims
        
        val exception = assertThrows(UnauthenticatedException::class.java) { service.revokeToken(token) }
        
        assertEquals(IdentityTokenErrorCode.INVALID_TOKEN, exception.errorCode)
        
        verify { refreshTokenRepository wasNot called }
        verify { passwordHasher wasNot called }
    }
    
    @Test
    fun `when token hash mismatched, revokeToken should throw UnauthenticatedException`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims()
        val refreshToken = AuthDomainFactory.createRefreshToken(userId = claims.userId, tokenHash = "token hash 2")
        
        every { identityTokenProvider.decode(token) } returns claims
        every { refreshTokenRepository.findAllByUserId(claims.userId) } returns listOf(refreshToken)
        every { passwordHasher.verify(token, refreshToken.tokenHash) } returns false
        
        val exception = assertThrows(UnauthenticatedException::class.java) { service.revokeToken(token) }
        
        assertEquals(IdentityTokenErrorCode.INVALID_TOKEN, exception.errorCode)
        
        verify(exactly = 0) { refreshTokenRepository.deleteById(any()) }
    }
    
    @Test
    fun `when not found tokens for user, revokeToken should throw UnauthenticatedException`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims()
        
        every { identityTokenProvider.decode(token) } returns claims
        every { refreshTokenRepository.findAllByUserId(claims.userId) } returns emptyList()
        
        val exception = assertThrows(UnauthenticatedException::class.java) { service.revokeToken(token) }
        
        assertEquals(IdentityTokenErrorCode.INVALID_TOKEN, exception.errorCode)
        
        verify(exactly = 0) { refreshTokenRepository.deleteById(any()) }
    }
    
    @Test
    fun `when token is valid, authenticateToken should return claims`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims()
        
        every { identityTokenProvider.decode(token) } returns claims
        
        val result = service.authenticateToken(token, IdentityTokenType.REFRESH)
        
        assertEquals(claims, result)
        assertEquals(claims.userId, result.userId)
    }
    
    @Test
    fun `when issuer is invalid, authenticateToken should throw InvalidIssuerError`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims(issuer = "wrong issuer")
        
        every { identityTokenProvider.decode(token) } returns claims
        
        val exception = assertThrows(UnauthenticatedException::class.java) {
            service.authenticateToken(token, IdentityTokenType.REFRESH)
        }
        
        assertEquals(IdentityTokenErrorCode.INVALID_ISSUER, exception.errorCode)
    }
    
    @Test
    fun `when token type is invalid, authenticateToken should throw InvalidTokenTypeError`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims()
        
        every { identityTokenProvider.decode(token) } returns claims
        
        val exception = assertThrows(UnauthenticatedException::class.java) {
            service.authenticateToken(token, IdentityTokenType.ACCESS)
        }
        
        assertEquals(IdentityTokenErrorCode.INVALID_TOKEN_TYPE, exception.errorCode)
    }
    
    @Test
    fun `when token is expired, authenticateToken should throw TokenExpiredError`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims(
            expiresAt = Instant.now().minusSeconds(1000)
        )
        
        every { identityTokenProvider.decode(token) } returns claims
        
        val exception = assertThrows(UnauthenticatedException::class.java) {
            service.authenticateToken(token, IdentityTokenType.REFRESH)
        }
        
        assertEquals(IdentityTokenErrorCode.TOKEN_EXPIRED, exception.errorCode)
    }
}