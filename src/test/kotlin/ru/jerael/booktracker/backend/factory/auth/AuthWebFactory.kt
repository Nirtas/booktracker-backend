package ru.jerael.booktracker.backend.factory.auth

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType
import ru.jerael.booktracker.backend.web.dto.auth.*
import java.util.*

object AuthWebFactory {
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
    
    fun createAuthToken(
        userId: UUID = UUID.randomUUID()
    ): UsernamePasswordAuthenticationToken {
        return UsernamePasswordAuthenticationToken(userId, null, emptyList())
    }
}