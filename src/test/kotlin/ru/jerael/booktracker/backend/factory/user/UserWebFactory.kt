package ru.jerael.booktracker.backend.factory.user

import ru.jerael.booktracker.backend.web.dto.user.UserCreationRequest
import ru.jerael.booktracker.backend.web.dto.user.UserCreationResponse
import ru.jerael.booktracker.backend.web.dto.user.UserResponse
import java.time.Instant
import java.util.*

object UserWebFactory {
    fun createUserCreationRequest(
        email: String = "test@example.com",
        password: String = "Password123!"
    ): UserCreationRequest {
        return UserCreationRequest(email, password)
    }
    
    fun createUserCreationResponse(
        userId: UUID = UUID.randomUUID(),
        email: String = "test@example.com",
        expiresAt: Instant = Instant.now().plusSeconds(600)
    ): UserCreationResponse {
        return UserCreationResponse(userId, email, expiresAt)
    }
    
    fun createUserResponse(
        userId: UUID = UUID.randomUUID(),
        email: String = "test@example.com",
        isVerified: Boolean = false,
        createdAt: Instant = Instant.now()
    ): UserResponse {
        return UserResponse(userId, email, isVerified, createdAt)
    }
}
