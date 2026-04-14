package ru.jerael.booktracker.backend.factory.author

import ru.jerael.booktracker.backend.web.dto.author.AuthorResponse
import java.util.*

object AuthorWebFactory {
    fun createAuthorResponse(
        id: UUID = UUID.randomUUID(),
        fullName: String = "Author A"
    ): AuthorResponse {
        return AuthorResponse(id, fullName)
    }
}