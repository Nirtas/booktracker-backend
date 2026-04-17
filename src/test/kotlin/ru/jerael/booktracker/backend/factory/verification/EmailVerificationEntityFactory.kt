package ru.jerael.booktracker.backend.factory.verification

import ru.jerael.booktracker.backend.data.db.entity.EmailVerificationEntity
import ru.jerael.booktracker.backend.data.db.entity.UserEntity
import ru.jerael.booktracker.backend.domain.model.verification.VerificationType
import java.time.Instant
import java.util.*

object EmailVerificationEntityFactory {
    fun createEmailVerificationEntity(
        id: UUID? = UUID.randomUUID(),
        user: UserEntity? = UserEntity().apply { this.id = UUID.randomUUID() },
        email: String = "test@example.com",
        type: VerificationType = VerificationType.REGISTRATION,
        token: String = "123456",
        expiresAt: Instant = Instant.now().plusSeconds(600),
        createdAt: Instant = Instant.now()
    ): EmailVerificationEntity {
        return EmailVerificationEntity().apply {
            this.id = id; this.user = user; this.email = email; this.type = type
            this.token = token; this.expiresAt = expiresAt; this.createdAt = createdAt
        }
    }
}
