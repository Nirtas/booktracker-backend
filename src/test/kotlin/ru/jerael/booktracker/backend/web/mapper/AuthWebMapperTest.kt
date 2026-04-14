package ru.jerael.booktracker.backend.web.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.auth.AuthDomainFactory
import ru.jerael.booktracker.backend.factory.auth.AuthWebFactory

class AuthWebMapperTest {
    private val mapper = AuthWebMapper()
    
    @Test
    fun `toDomain should map ConfirmRegistrationRequest to ConfirmRegistration`() {
        val request = AuthWebFactory.createConfirmRegistrationRequest()
        
        val domain = mapper.toDomain(request)
        
        with(domain) {
            assertEquals(request.userId, userId)
            assertEquals(request.token, token)
        }
    }
    
    @Test
    fun `toResponse should map TokenPair to AuthResponse`() {
        val tokenPair = AuthDomainFactory.createTokenPair()
        
        val response = mapper.toResponse(tokenPair)
        
        with(response) {
            assertEquals(tokenPair.accessToken, accessToken)
            assertEquals(tokenPair.refreshToken, refreshToken)
        }
    }
    
    @Test
    fun `toDomain should map LoginRequest to UserLogin`() {
        val request = AuthWebFactory.createLoginRequest()
        
        val domain = mapper.toDomain(request)
        
        with(domain) {
            assertEquals(request.email, email)
            assertEquals(request.password, password)
        }
    }
    
    @Test
    fun `toDomain should map RefreshTokensRequest to RefreshTokenPayload`() {
        val request = AuthWebFactory.createRefreshTokensRequest()
        
        val domain = mapper.toDomain(request)
        
        with(domain) {
            assertEquals(request.refreshToken, refreshToken)
        }
    }
    
    @Test
    fun `toDomain should map LogoutRequest to LogoutPayload`() {
        val request = AuthWebFactory.createLogoutRequest()
        
        val domain = mapper.toDomain(request)
        
        with(domain) {
            assertEquals(request.refreshToken, refreshToken)
        }
    }
    
    @Test
    fun `toDomain should map ResendVerificationRequest to ResendVerification`() {
        val request = AuthWebFactory.createResendVerificationRequest()
        
        val domain = mapper.toDomain(request)
        
        with(domain) {
            assertEquals(request.userId, userId)
            assertEquals(request.type, type.name)
        }
    }
    
    @Test
    fun `toResponse should map ResendVerificationResult to ResendVerificationResponse`() {
        val result = AuthDomainFactory.createResendVerificationResult()
        
        val response = mapper.toResponse(result)
        
        with(response) {
            assertEquals(result.userId, userId)
            assertEquals(result.email, email)
            assertEquals(result.expiresAt, expiresAt)
        }
    }
}