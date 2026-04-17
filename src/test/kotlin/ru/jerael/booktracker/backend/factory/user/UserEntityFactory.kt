package ru.jerael.booktracker.backend.factory.user

import ru.jerael.booktracker.backend.data.db.entity.UserEntity
import java.time.Instant
import java.util.*

object UserEntityFactory {
    fun createUserEntity(
        id: UUID? = null,
        email: String = "test@example.com",
        passwordHash: String = "password hash",
        isVerified: Boolean = false,
        createdAt: Instant = Instant.now()
    ): UserEntity {
        return UserEntity().apply {
            this.id = id; this.email = email; this.passwordHash = passwordHash
            this.isVerified = isVerified; this.createdAt = createdAt
        }
    }
}
