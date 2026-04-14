package ru.jerael.booktracker.backend.factory.verification

import ru.jerael.booktracker.backend.domain.model.verification.*
import java.time.Duration
import java.time.Instant
import java.util.*

object EmailVerificationDomainFactory {
    fun createEmailVerification(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        email: String = "test@example.com",
        type: VerificationType = VerificationType.REGISTRATION,
        token: String = "123456",
        expiresAt: Instant = Instant.now().plusSeconds(600),
        createdAt: Instant = Instant.now()
    ): EmailVerification {
        return EmailVerification(id, userId, email, type, token, expiresAt, createdAt)
    }
    
    fun createEmailVerificationConfirmation(
        userId: UUID = UUID.randomUUID(),
        token: String = "123456",
        type: VerificationType = VerificationType.REGISTRATION
    ): EmailVerificationConfirmation {
        return EmailVerificationConfirmation(userId, token, type)
    }
    
    fun createEmailVerificationInitiation(
        userId: UUID = UUID.randomUUID(),
        email: String = "test@example.com",
        type: VerificationType = VerificationType.REGISTRATION
    ): EmailVerificationInitiation {
        return EmailVerificationInitiation(userId, email, type)
    }
    
    fun createVerificationToken(
        value: String = "123456",
        expiry: Duration = Duration.ofMinutes(10)
    ): VerificationToken {
        return VerificationToken(value, expiry)
    }
}
