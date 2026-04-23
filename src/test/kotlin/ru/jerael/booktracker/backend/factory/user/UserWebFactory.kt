package ru.jerael.booktracker.backend.factory.user

import ru.jerael.booktracker.backend.web.dto.user.UserCreationRequest

object UserWebFactory {
    fun createUserCreationRequest(
        email: String = "test@example.com",
        password: String = "Password123!"
    ): UserCreationRequest {
        return UserCreationRequest(email, password)
    }
}
