package ru.jerael.booktracker.backend.factory.book

import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.web.dto.author.AuthorResponse
import ru.jerael.booktracker.backend.web.dto.book.BookCreationRequest
import ru.jerael.booktracker.backend.web.dto.book.BookDetailsUpdateRequest
import ru.jerael.booktracker.backend.web.dto.book.BookResponse
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse
import ru.jerael.booktracker.backend.web.dto.language.LanguageResponse
import ru.jerael.booktracker.backend.web.dto.note.NoteResponse
import ru.jerael.booktracker.backend.web.dto.publisher.PublisherResponse
import ru.jerael.booktracker.backend.web.dto.reading_attempt.ReadingAttemptResponse
import java.time.Instant
import java.util.*

object BookWebFactory {
    fun createBookCreationRequest(
        title: String? = " title ",
        status: String? = BookStatus.defaultStatus().value,
        genreIds: Set<Int> = setOf(1, 2),
        authorNames: Set<String> = setOf("Author A", "Author B"),
        description: String? = "Description",
        publisherName: String? = "Publisher A",
        languageCode: String? = "en",
        publishedOn: Int? = 2025,
        totalPages: Int? = 300,
        isbn10: String? = "123-456-789-0",
        isbn13: String? = "123-456-789-012-3"
    ): BookCreationRequest {
        return BookCreationRequest(
            title, status, genreIds, authorNames, description, publisherName,
            languageCode, publishedOn, totalPages, isbn10, isbn13
        )
    }
    
    fun createBookDetailsUpdateRequest(
        title: String? = "New Title",
        status: String? = BookStatus.defaultStatus().value,
        genreIds: Set<Int> = setOf(1, 2),
        authorNames: Set<String> = setOf("Author A", "Author B"),
        description: String? = "Description",
        publisherName: String? = "Publisher A",
        languageCode: String? = "en",
        publishedOn: Int? = 1000,
        totalPages: Int? = 678,
        isbn10: String? = "123-456-789-0",
        isbn13: String? = "123-456-789-012-3"
    ): BookDetailsUpdateRequest {
        return BookDetailsUpdateRequest(
            title, status, genreIds, authorNames, description, publisherName,
            languageCode, publishedOn, totalPages, isbn10, isbn13
        )
    }
    
    fun createBookResponse(
        id: UUID = UUID.randomUUID(),
        title: String? = "Title",
        coverUrl: String? = null,
        status: String? = null,
        createdAt: Instant = Instant.now(),
        genres: Set<GenreResponse> = emptySet(),
        authors: Set<AuthorResponse> = emptySet(),
        description: String? = null,
        publisher: PublisherResponse? = null,
        language: LanguageResponse? = null,
        publishedOn: Int? = 2025,
        totalPages: Int? = 300,
        isbn10: String? = null,
        isbn13: String? = null,
        attempts: List<ReadingAttemptResponse> = emptyList(),
        notes: List<NoteResponse> = emptyList()
    ): BookResponse {
        return BookResponse(
            id, title, coverUrl, status, createdAt, genres, authors, description,
            publisher, language, publishedOn, totalPages, isbn10, isbn13, attempts, notes
        )
    }
}
