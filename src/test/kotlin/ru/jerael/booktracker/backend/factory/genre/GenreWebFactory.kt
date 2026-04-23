package ru.jerael.booktracker.backend.factory.genre

import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse

object GenreWebFactory {
    fun createGenreResponse(
        id: Int = 1,
        name: String = "Genre 1"
    ): GenreResponse {
        return GenreResponse(id, name)
    }
}