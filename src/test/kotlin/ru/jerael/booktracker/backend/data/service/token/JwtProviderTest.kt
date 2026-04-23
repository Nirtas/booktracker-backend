package ru.jerael.booktracker.backend.data.service.token

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.data.service.token.config.JwtProperties
import ru.jerael.booktracker.backend.domain.exception.UnauthenticatedException
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory
import java.time.temporal.ChronoUnit

class JwtProviderTest {
    private val jwtProvider = JwtProvider(
        JwtProperties().apply {
            secret = "a".repeat(32)
        }
    )
    
    @Test
    fun `encode should create valid signed jwt`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims()
        
        val token = jwtProvider.encode(claims)
        
        assertEquals(3, token.split(".").size)
    }
    
    @Test
    fun `when token is valid, decode should return correct claims`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims()
        val token = jwtProvider.encode(claims)
        
        val result = jwtProvider.decode(token)
        
        with(result) {
            assertEquals(claims.userId, userId)
            assertEquals(claims.issuer, issuer)
            assertEquals(claims.type, type)
            assertEquals(claims.issuedAt.truncatedTo(ChronoUnit.SECONDS), issuedAt)
            assertEquals(claims.expiresAt.truncatedTo(ChronoUnit.SECONDS), expiresAt)
        }
    }
    
    @Test
    fun `when signature is invalid, decode should throw UnauthenticatedException`() {
        val claims = AuthDomainFactory.createIdentityTokenClaims()
        val validToken = jwtProvider.encode(claims)
        val invalidToken = validToken.substring(0, validToken.length - 10)
        
        val exception = assertThrows(UnauthenticatedException::class.java) {
            jwtProvider.decode(invalidToken)
        }
        
        assertThat(exception.message).contains("Token signature is invalid")
    }
    
    @Test
    fun `when signature is malformed, decode should throw UnauthenticatedException`() {
        val malformedToken = "not a jwt string"
        
        val exception = assertThrows(UnauthenticatedException::class.java) {
            jwtProvider.decode(malformedToken)
        }
        
        assertThat(exception.message).contains("Token is invalid or corrupted")
    }
}