package ru.jerael.booktracker.backend.factory.user

import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.model.user.UserCreation
import ru.jerael.booktracker.backend.domain.model.user.UserCreationResult
import java.time.Instant
import java.util.*

object UserDomainFactory {
    fun createUser(
        userId: UUID = UUID.randomUUID(),
        email: String = "test@example.com",
        passwordHash: String = "password hash",
        isVerified: Boolean = false,
        createdAt: Instant = Instant.now()
    ): User {
        return User(userId, email, passwordHash, isVerified, createdAt)
    }
    
    fun createUserCreation(
        email: String = "test@example.com",
        password: String = "Password123!"
    ): UserCreation {
        return UserCreation(email, password)
    }
    
    fun createUserCreationResult(
        userId: UUID = UUID.randomUUID(),
        email: String = "test@example.com",
        expiresAt: Instant = Instant.now().plusSeconds(600)
    ): UserCreationResult {
        return UserCreationResult(userId, email, expiresAt)
    }
}
