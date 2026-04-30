package ru.jerael.booktracker.backend.factory.book

import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.web.dto.author.AuthorResponse
import ru.jerael.booktracker.backend.web.dto.book.BookCreationRequest
import ru.jerael.booktracker.backend.web.dto.book.BookDetailsUpdateRequest
import ru.jerael.booktracker.backend.web.dto.book.BookMetadataResponse
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
        title: String? = null,
        status: String? = null,
        genreIds: Set<Int>? = null,
        authorNames: Set<String>? = null,
        description: String? = null,
        publisherName: String? = null,
        languageCode: String? = null,
        publishedOn: Int? = null,
        totalPages: Int? = null,
        isbn10: String? = null,
        isbn13: String? = null
    ): BookDetailsUpdateRequest {
        return BookDetailsUpdateRequest(
            title, status, genreIds, authorNames, description, publisherName,
            languageCode, publishedOn, totalPages, isbn10, isbn13
        )
    }
    
    fun createBookResponse(
        id: UUID = UUID.randomUUID(),
        title: String? = "Title",
        coverUrl: String? = "cover.jpg",
        status: String? = BookStatus.defaultStatus().value,
        createdAt: Instant = Instant.now(),
        genres: Set<GenreResponse> = setOf(
            GenreResponse(1, "action"),
            GenreResponse(2, "adventure")
        ),
        authors: Set<AuthorResponse> = setOf(
            AuthorResponse(UUID.randomUUID(), "Author A"),
            AuthorResponse(UUID.randomUUID(), "Author B")
        ),
        description: String? = "Description",
        publisher: PublisherResponse? = PublisherResponse(UUID.randomUUID(), "Publisher A"),
        language: LanguageResponse? = LanguageResponse("en", "English"),
        publishedOn: Int? = 2025,
        totalPages: Int? = 300,
        isbn10: String? = "1234567890",
        isbn13: String? = "1234567890123",
        attempts: List<ReadingAttemptResponse> = listOf(
            ReadingAttemptResponse(
                UUID.randomUUID(), BookStatus.defaultStatus().value,
                Instant.now(), null, emptyList()
            )
        ),
        notes: List<NoteResponse> = emptyList()
    ): BookResponse {
        return BookResponse(
            id, title, coverUrl, status, createdAt, genres, authors, description,
            publisher, language, publishedOn, totalPages, isbn10, isbn13, attempts, notes
        )
    }
    
    fun createBookMetadataResponse(
        title: String? = "Title",
        cover: String? = null,
        genres: Set<GenreResponse> = emptySet(),
        authors: Set<AuthorResponse> = emptySet(),
        description: String? = null,
        publisher: PublisherResponse? = null,
        language: LanguageResponse? = null,
        publishedOn: Int? = 2025,
        totalPages: Int? = 300,
        isbn10: String? = null,
        isbn13: String? = null
    ): BookMetadataResponse {
        return BookMetadataResponse(
            title, cover, genres, authors, description,
            publisher, language, publishedOn, totalPages, isbn10, isbn13
        )
    }
}
