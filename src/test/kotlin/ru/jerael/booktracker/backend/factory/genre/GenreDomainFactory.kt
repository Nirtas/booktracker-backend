package ru.jerael.booktracker.backend.factory.genre

import ru.jerael.booktracker.backend.domain.model.genre.Genre

object GenreDomainFactory {
    fun createGenre(
        id: Int? = 1,
        name: String = "Genre 1"
    ): Genre {
        return Genre(id, name)
    }
}