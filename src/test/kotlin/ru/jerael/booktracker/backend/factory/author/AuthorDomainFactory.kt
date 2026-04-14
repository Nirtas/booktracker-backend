package ru.jerael.booktracker.backend.factory.author

import ru.jerael.booktracker.backend.domain.model.author.Author
import java.util.*

object AuthorDomainFactory {
    fun createAuthor(
        id: UUID = UUID.randomUUID(),
        fullName: String = "Author A"
    ): Author {
        return Author(id, fullName)
    }
}