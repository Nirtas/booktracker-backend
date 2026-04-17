package ru.jerael.booktracker.backend.factory.book

import ru.jerael.booktracker.backend.domain.model.Genre
import ru.jerael.booktracker.backend.domain.model.author.Author
import ru.jerael.booktracker.backend.domain.model.book.*
import ru.jerael.booktracker.backend.domain.model.language.Language
import ru.jerael.booktracker.backend.domain.model.note.Note
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt
import java.io.InputStream
import java.time.Instant
import java.util.*

object BookDomainFactory {
    fun createBook(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        title: String? = "Title",
        coverFileName: String? = "cover.jpg",
        createdAt: Instant = Instant.now(),
        genres: Set<Genre> = setOf(
            Genre(1, "action"),
            Genre(2, "adventure")
        ),
        authors: Set<Author> = setOf(
            Author(UUID.randomUUID(), "Author A"),
            Author(UUID.randomUUID(), "Author B")
        ),
        description: String? = "Description",
        publisher: Publisher? = Publisher(UUID.randomUUID(), "Publisher A"),
        language: Language? = Language("en", "English"),
        publishedOn: Int? = 2025,
        totalPages: Int? = 300,
        isbn10: String? = "1234567890",
        isbn13: String? = "1234567890123",
        attempts: List<ReadingAttempt> = listOf(
            ReadingAttempt(
                UUID.randomUUID(), null, BookStatus.defaultStatus(),
                Instant.now(), null, emptyList()
            )
        ),
        notes: List<Note> = emptyList()
    ): Book {
        return Book(
            id, userId, title, coverFileName, createdAt, genres, authors, description,
            publisher, language, publishedOn, totalPages, isbn10, isbn13, attempts, notes
        )
    }
    
    fun createBookCreation(
        userId: UUID = UUID.randomUUID(),
        title: String? = "title",
        status: BookStatus = BookStatus.defaultStatus(),
        genreIds: Set<Int> = setOf(1, 2),
        authorNames: Set<String> = setOf("Author A", "Author B"),
        description: String? = "Description",
        publisherName: String? = "Publisher A",
        languageCode: String? = "en",
        publishedOn: Int? = 2025,
        totalPages: Int? = 300,
        isbn10: String? = "1234567890",
        isbn13: String? = "1234567890123"
    ): BookCreation {
        return BookCreation(
            userId, title, status, genreIds, authorNames, description,
            publisherName, languageCode, publishedOn, totalPages, isbn10, isbn13
        )
    }
    
    fun createBookDetailsUpdate(
        bookId: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        title: String? = null,
        status: BookStatus? = null,
        genreIds: Set<Int>? = null,
        authorNames: Set<String>? = null,
        description: String? = null,
        publisherName: String? = null,
        languageCode: String? = null,
        publishedOn: Int? = null,
        totalPages: Int? = null,
        isbn10: String? = null,
        isbn13: String? = null
    ): BookDetailsUpdate {
        return BookDetailsUpdate(
            bookId, userId, title, status, genreIds, authorNames, description,
            publisherName, languageCode, publishedOn, totalPages, isbn10, isbn13
        )
    }
    
    fun createBookDetailsUpdateFilled(
        bookId: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        title: String? = "title",
        status: BookStatus? = BookStatus.defaultStatus(),
        genreIds: Set<Int>? = setOf(1, 2),
        authorNames: Set<String>? = setOf("Author A", "Author B"),
        description: String? = "Description",
        publisherName: String? = "Publisher A",
        languageCode: String? = "en",
        publishedOn: Int? = 2025,
        totalPages: Int? = 300,
        isbn10: String? = "1234567890",
        isbn13: String? = "1234567890123"
    ): BookDetailsUpdate {
        return BookDetailsUpdate(
            bookId, userId, title, status, genreIds, authorNames, description,
            publisherName, languageCode, publishedOn, totalPages, isbn10, isbn13
        )
    }
    
    fun createUploadCoverPayload(
        bookId: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        contentType: String? = "image/jpeg",
        content: InputStream? = "cover content".toByteArray().inputStream(),
        size: Long = "cover content".length.toLong()
    ): UploadCoverPayload {
        return UploadCoverPayload(bookId, userId, contentType, content, size)
    }
}
