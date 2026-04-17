package ru.jerael.booktracker.backend.factory.genre

import ru.jerael.booktracker.backend.data.db.entity.GenreEntity

object GenreEntityFactory {
    fun createGenreEntity(
        id: Int? = 1,
        name: String = "Genre 1"
    ): GenreEntity {
        return GenreEntity().apply { this.id = id; this.name = name }
    }
}