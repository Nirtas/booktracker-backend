package ru.jerael.booktracker.backend.factory.author

import ru.jerael.booktracker.backend.data.db.entity.AuthorEntity
import java.util.*

object AuthorEntityFactory {
    fun createAuthorEntity(
        id: UUID? = UUID.randomUUID(),
        fullName: String = "Author A"
    ): AuthorEntity {
        return AuthorEntity().apply { this.id = id; this.fullName = fullName }
    }
}