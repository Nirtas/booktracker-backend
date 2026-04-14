package ru.jerael.booktracker.backend.factory.auth

import ru.jerael.booktracker.backend.domain.model.verification.VerificationType
import ru.jerael.booktracker.backend.web.dto.auth.*
import java.time.Instant
import java.util.*

object AuthWebFactory {
    fun createAuthResponse(
        accessToken: String = "access token",
        refreshToken: String = "refresh token"
    ): AuthResponse {
        return AuthResponse(accessToken, refreshToken)
    }
    
    fun createConfirmRegistrationRequest(
        userId: UUID = UUID.randomUUID(),
        token: String = "123456"
    ): ConfirmRegistrationRequest {
        return ConfirmRegistrationRequest(userId, token)
    }
    
    fun createLoginRequest(
        email: String = "test@example.com",
        password: String = "Password123!"
    ): LoginRequest {
        return LoginRequest(email, password)
    }
    
    fun createLogoutRequest(
        refreshToken: String = "refresh token"
    ): LogoutRequest {
        return LogoutRequest(refreshToken)
    }
    
    fun createRefreshTokensRequest(
        refreshToken: String = "refresh token"
    ): RefreshTokensRequest {
        return RefreshTokensRequest(refreshToken)
    }
    
    fun createResendVerificationRequest(
        userId: UUID = UUID.randomUUID(),
        type: String = VerificationType.REGISTRATION.name
    ): ResendVerificationRequest {
        return ResendVerificationRequest(userId, type)
    }
    
    fun createResendVerificationResponse(
        userId: UUID = UUID.randomUUID(),
        email: String = "test@example.com",
        expiresAt: Instant = Instant.now().plusSeconds(600)
    ): ResendVerificationResponse {
        return ResendVerificationResponse(userId, email, expiresAt)
    }
}