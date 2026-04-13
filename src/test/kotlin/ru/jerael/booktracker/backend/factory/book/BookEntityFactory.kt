package ru.jerael.booktracker.backend.factory.book

import ru.jerael.booktracker.backend.data.db.entity.*
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import java.time.Instant
import java.util.*

object BookEntityFactory {
    fun createBookEntity(
        id: UUID = UUID.randomUUID(),
        user: UserEntity = UserEntity().apply { this.id = UUID.randomUUID() },
        title: String = "Title",
        coverFileName: String? = "cover.jpg",
        createdAt: Instant = Instant.now(),
        genres: Set<GenreEntity> = setOf(
            GenreEntity().apply { this.id = 1; this.name = "Genre 1" },
            GenreEntity().apply { this.id = 2; this.name = "Genre 2" }
        ),
        authors: Set<AuthorEntity> = setOf(
            AuthorEntity().apply { this.id = UUID.randomUUID(); this.fullName = "Author A" },
            AuthorEntity().apply { this.id = UUID.randomUUID(); this.fullName = "Author B" }
        ),
        description: String? = "Description",
        publisher: PublisherEntity? = PublisherEntity().apply {
            this.id = UUID.randomUUID(); this.name = "Publisher A"
        },
        language: LanguageEntity? = LanguageEntity().apply { this.code = "en"; this.name = "English" },
        publishedOn: Int? = 2025,
        totalPages: Int? = 300,
        isbn10: String? = "123-456-789-0",
        isbn13: String? = "123-456-789-012-3",
        attempts: List<ReadingAttemptEntity> = listOf(
            ReadingAttemptEntity().apply {
                this.id = UUID.randomUUID()
                this.book = BookEntity().apply { this.id = UUID.randomUUID() }
                this.status = BookStatus.defaultStatus()
                this.startedAt = Instant.now()
                this.finishedAt = null
                this.sessions = emptyList()
            }
        ),
        notes: List<NoteEntity> = emptyList()
    ): BookEntity {
        return BookEntity().apply {
            this.id = id; this.user = user; this.title = title; this.coverFileName = coverFileName
            this.createdAt = createdAt; this.genres = genres; this.authors = authors; this.description = description
            this.publisher = publisher; this.language = language; this.publishedOn = publishedOn
            this.totalPages = totalPages; this.isbn10 = isbn10; this.isbn13 = isbn13
            this.attempts = attempts
            this.notes = notes
        }
    }
}
