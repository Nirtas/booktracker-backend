package ru.jerael.booktracker.backend.factory.book

import ru.jerael.booktracker.backend.data.db.entity.*
import java.time.Instant
import java.util.*

object BookEntityFactory {
    fun createBookEntity(
        id: UUID? = null,
        user: UserEntity? = null,
        title: String = "Title",
        coverFileName: String? = "cover.jpg",
        createdAt: Instant = Instant.now(),
        genres: Set<GenreEntity> = emptySet(),
        authors: Set<AuthorEntity> = emptySet(),
        description: String? = "Description",
        publisher: PublisherEntity? = null,
        language: LanguageEntity? = null,
        publishedOn: Int? = 2025,
        totalPages: Int? = 300,
        isbn10: String? = "1234567890",
        isbn13: String? = "1234567890123",
        attempts: List<ReadingAttemptEntity> = emptyList(),
        notes: List<NoteEntity> = emptyList()
    ): BookEntity {
        return BookEntity().apply {
            this.id = id; this.user = user; this.title = title; this.coverFileName = coverFileName
            this.createdAt = createdAt; this.genres = genres; this.authors = authors; this.description = description
            this.publisher = publisher; this.language = language; this.publishedOn = publishedOn
            this.totalPages = totalPages; this.isbn10 = isbn10; this.isbn13 = isbn13
            this.attempts = attempts
            this.notes = notes
            this.attempts.forEach { it.book = this }
        }
    }
}
