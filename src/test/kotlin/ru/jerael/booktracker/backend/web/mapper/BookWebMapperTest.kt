package ru.jerael.booktracker.backend.web.mapper

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import ru.jerael.booktracker.backend.factory.book.BookWebFactory
import ru.jerael.booktracker.backend.web.util.LinkBuilder

@ExtendWith(MockKExtension::class)
class BookWebMapperTest {
    private val genreWebMapper = GenreWebMapper()
    private val authorWebMapper = AuthorWebMapper()
    private val publisherWebMapper = PublisherWebMapper()
    private val languageWebMapper = LanguageWebMapper()
    private val readingSessionWebMapper = ReadingSessionWebMapper()
    private val readingAttemptWebMapper = ReadingAttemptWebMapper(readingSessionWebMapper)
    private val noteWebMapper = NoteWebMapper()
    private val linkBuilder = mockk<LinkBuilder>()
    
    @InjectMockKs
    private lateinit var mapper: BookWebMapper
    
    @Test
    fun `toResponse should map Book to BookResponse`() {
        val book = BookDomainFactory.createBook(coverFileName = "cover.jpg")
        val coverUrl = "http://localhost:8080/api/v1/books/${book.id}/cover"
        every { linkBuilder.buildCoverUrl(book.id) } returns coverUrl
        
        val bookResponse = mapper.toResponse(book)
        
        with(bookResponse) {
            assertEquals(book.id, id)
            assertEquals(book.title, title)
            assertEquals(coverUrl, this.coverUrl)
            assertEquals(book.status().value, status)
            assertEquals(book.createdAt, createdAt)
            
            val expectedGenres = genreWebMapper.toResponses(book.genres)
            assertEquals(expectedGenres, genres)
            
            val expectedAuthors = book.authors.map { authorWebMapper.toResponse(it) }.toSet()
            assertEquals(expectedAuthors, authors)
            
            assertEquals(book.description, description)
            
            val expectedPublisher = publisherWebMapper.toResponse(book.publisher)
            assertEquals(expectedPublisher, publisher)
            
            val expectedLanguage = languageWebMapper.toResponse(book.language)
            assertEquals(expectedLanguage, language)
            
            assertEquals(book.publishedOn, publishedOn)
            assertEquals(book.totalPages, totalPages)
            assertEquals(book.isbn10, isbn10)
            assertEquals(book.isbn13, isbn13)
            
            val expectedReadingAttempts = book.attempts.map { readingAttemptWebMapper.toResponse(it) }
            assertEquals(expectedReadingAttempts, attempts)
            
            val expectedNotes = book.notes.map { noteWebMapper.toResponse(it) }
            assertEquals(expectedNotes, notes)
        }
    }
    
    @Test
    fun `toResponses should map Books to BookResponses`() {
        val book = BookDomainFactory.createBook(coverFileName = "cover.jpg")
        val book2 = BookDomainFactory.createBook(userId = book.userId, title = "asd", coverFileName = "cover.jpg")
        every { linkBuilder.buildCoverUrl(book.id) } returns "url1"
        every { linkBuilder.buildCoverUrl(book2.id) } returns "url2"
        
        val bookResponses = mapper.toResponses(listOf(book, book2))
        
        with(bookResponses) {
            assertEquals(2, size)
            
            assertEquals(book.id, this[0].id)
            assertEquals(book.title, this[0].title)
            assertEquals("url1", this[0].coverUrl)
            
            assertEquals(book2.id, this[1].id)
            assertEquals(book2.title, this[1].title)
            assertEquals("url2", this[1].coverUrl)
        }
    }
    
    @Test
    fun `toDomain should map BookCreationRequest with userId to BookCreation`() {
        val book = BookDomainFactory.createBook()
        val request = BookWebFactory.createBookCreationRequest(
            isbn10 = "123-456-789-0",
            isbn13 = "123-456-789-012-3"
        )
        
        val data = mapper.toDomain(request, book.userId)
        
        with(data) {
            assertEquals(book.userId, userId)
            assertEquals(request.title.trim(), title)
            assertEquals(BookStatus.fromString(request.status), status)
            assertEquals(request.genreIds, genreIds)
            assertEquals(request.authorNames, authorNames)
            assertEquals(request.description, description)
            assertEquals(request.publisherName, publisherName)
            assertEquals(request.languageCode, languageCode)
            assertEquals(request.publishedOn, publishedOn)
            assertEquals(request.totalPages, totalPages)
            assertEquals("1234567890", isbn10)
            assertEquals("1234567890123", isbn13)
        }
    }
    
    @Test
    fun `toDomain should map BookDetailsUpdateRequest with bookId and userId to BookDetailsUpdate`() {
        val book = BookDomainFactory.createBook()
        val request = BookWebFactory.createBookDetailsUpdateRequest(
            isbn10 = "123-456-789-0",
            isbn13 = "123-456-789-012-3"
        )
        
        val data = mapper.toDomain(request, book.id, book.userId)
        
        with(data) {
            assertEquals(book.userId, userId)
            assertEquals(request.title, title)
            assertEquals(BookStatus.fromString(request.status), status)
            assertEquals(request.genreIds, genreIds)
            assertEquals(request.authorNames, authorNames)
            assertEquals(request.description, description)
            assertEquals(request.publisherName, publisherName)
            assertEquals(request.languageCode, languageCode)
            assertThat(publishedOn as Int?).isEqualTo(request.publishedOn as Int?)
            assertThat(totalPages as Int?).isEqualTo(request.totalPages as Int?)
            assertEquals("1234567890", isbn10)
            assertEquals("1234567890123", isbn13)
        }
    }
    
    @Test
    fun `toResponse should map BookMetadata to BookMetadataResponse`() {
        val metadata = BookDomainFactory.createBookMetadata()
        
        val result = mapper.toResponse(metadata)
        
        with(result) {
            assertThat(title).isEqualTo(metadata.title)
            assertThat(coverUrl).isEqualTo(metadata.cover)
            
            val expectedGenres = genreWebMapper.toResponses(metadata.genres)
            assertThat(genres).isEqualTo(expectedGenres)
            
            val expectedAuthors = metadata.authors.map { authorWebMapper.toResponse(it) }.toSet()
            assertThat(authors).isEqualTo(expectedAuthors)
            
            assertThat(description).isEqualTo(metadata.description)
            
            val expectedPublisher = publisherWebMapper.toResponse(metadata.publisher)
            assertThat(publisher).isEqualTo(expectedPublisher)
            
            val expectedLanguage = languageWebMapper.toResponse(metadata.language)
            assertThat(language).isEqualTo(expectedLanguage)
            
            assertThat(publishedOn).isEqualTo(metadata.publishedOn)
            assertThat(totalPages).isEqualTo(metadata.totalPages)
            assertThat(isbn10).isEqualTo(metadata.isbn10)
            assertThat(isbn13).isEqualTo(metadata.isbn13)
        }
    }
}
