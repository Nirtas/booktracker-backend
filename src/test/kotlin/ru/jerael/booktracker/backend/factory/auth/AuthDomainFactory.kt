package ru.jerael.booktracker.backend.factory.auth

import ru.jerael.booktracker.backend.domain.model.auth.*
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType
import java.time.Instant
import java.util.*

object AuthDomainFactory {
    fun createConfirmRegistration(
        userId: UUID? = UUID.randomUUID(),
        token: String = "123456"
    ): ConfirmRegistration {
        return ConfirmRegistration(userId, token)
    }
    
    fun createIdentityTokenClaims(
        userId: UUID = UUID.randomUUID(),
        type: IdentityTokenType = IdentityTokenType.REFRESH,
        issuer: String = "issuer",
        issuedAt: Instant = Instant.now(),
        expiresAt: Instant = Instant.now().plusSeconds(6000)
    ): IdentityTokenClaims {
        return IdentityTokenClaims(userId, type, issuer, issuedAt, expiresAt)
    }
    
    fun createLogoutPayload(
        refreshToken: String = "refresh token"
    ): LogoutPayload {
        return LogoutPayload(refreshToken)
    }
    
    fun createRefreshToken(
        id: UUID? = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        tokenHash: String = "token hash",
        expiresAt: Instant = Instant.now().plusSeconds(6000)
    ): RefreshToken {
        return RefreshToken(id, userId, tokenHash, expiresAt)
    }
    
    fun createRefreshTokenPayload(
        refreshToken: String = "refresh token"
    ): RefreshTokenPayload {
        return RefreshTokenPayload(refreshToken)
    }
    
    fun createResendVerification(
        userId: UUID? = UUID.randomUUID(),
        type: VerificationType? = VerificationType.REGISTRATION
    ): ResendVerification {
        return ResendVerification(userId, type)
    }
    
    fun createResendVerificationResult(
        userId: UUID = UUID.randomUUID(),
        email: String = "test@example.com",
        expiresAt: Instant = Instant.now().plusSeconds(600)
    ): ResendVerificationResult {
        return ResendVerificationResult(userId, email, expiresAt)
    }
    
    fun createTokenPair(
        accessToken: String = "access token",
        refreshToken: String = "refresh token"
    ): TokenPair {
        return TokenPair(accessToken, refreshToken)
    }
    
    fun createUserLogin(
        email: String = "test@example.com",
        password: String = "Password123!"
    ): UserLogin {
        return UserLogin(email, password)
    }
}
