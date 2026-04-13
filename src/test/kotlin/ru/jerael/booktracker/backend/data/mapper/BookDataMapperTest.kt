package ru.jerael.booktracker.backend.data.mapper

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import ru.jerael.booktracker.backend.factory.book.BookEntityFactory

@ExtendWith(MockKExtension::class)
class BookDataMapperTest {
    private val genreDataMapper = GenreDataMapper()
    private val authorDataMapper = AuthorDataMapper()
    private val publisherDataMapper = PublisherDataMapper()
    private val languageDataMapper = LanguageDataMapper()
    private val readingSessionDataMapper = ReadingSessionDataMapper()
    private val readingAttemptDataMapper = ReadingAttemptDataMapper(readingSessionDataMapper)
    private val noteDataMapper = NoteDataMapper()
    
    @InjectMockKs
    private lateinit var mapper: BookDataMapper
    
    @Test
    fun `toDomain should map BookEntity to Book`() {
        val entity = BookEntityFactory.createBookEntity()
        
        val domain = mapper.toDomain(entity)
        
        with(domain) {
            assertEquals(entity.id, id)
            assertEquals(entity.user.id, userId)
            assertEquals(entity.title, title)
            assertEquals(entity.coverFileName, coverFileName)
            assertEquals(entity.createdAt, createdAt)
            
            val expectedGenres = entity.genres.map { genreDataMapper.toDomain(it) }.toSet()
            assertEquals(expectedGenres, genres)
            
            val expectedAuthors = entity.authors.map { authorDataMapper.toDomain(it) }.toSet()
            assertEquals(expectedAuthors, authors)
            
            assertEquals(entity.description, description)
            
            val expectedPublisher = publisherDataMapper.toDomain(entity.publisher)
            assertEquals(expectedPublisher, publisher)
            
            val expectedLanguage = languageDataMapper.toDomain(entity.language)
            assertEquals(expectedLanguage, language)
            
            assertEquals(entity.publishedOn, publishedOn)
            assertEquals(entity.totalPages, totalPages)
            assertEquals(entity.isbn10, isbn10)
            assertEquals(entity.isbn13, isbn13)
            
            val expectedReadingAttempts = entity.attempts.map { readingAttemptDataMapper.toDomain(it) }
            assertEquals(expectedReadingAttempts, attempts)
            
            val expectedNotes = entity.notes.map { noteDataMapper.toDomain(it) }
            assertEquals(expectedNotes, notes)
        }
    }
    
    @Test
    fun `toEntity should map Book to BookEntity`() {
        val book = BookDomainFactory.createBook()
        
        val entity = mapper.toEntity(book)
        
        with(entity) {
            assertEquals(book.id, id)
            assertEquals(book.userId, user.id)
            assertEquals(book.title, title)
            assertEquals(book.coverFileName, coverFileName)
            assertEquals(book.createdAt, createdAt)
            
            val expectedGenreIds = book.genres.map { it.id }.toSet()
            val actualGenreIds = genres.map { it.id }.toSet()
            assertEquals(expectedGenreIds, actualGenreIds)
            
            val expectedAuthorIds = book.authors.map { it.id }.toSet()
            val actualAuthorIds = authors.map { it.id }.toSet()
            assertEquals(expectedAuthorIds, actualAuthorIds)
            
            assertEquals(book.description, description)
            
            assertEquals(book.publisher.id, publisher.id)
            assertEquals(book.publisher.name, publisher.name)
            
            assertEquals(book.language.code, language.code)
            assertEquals(book.language.name, language.name)
            
            assertEquals(book.publishedOn, publishedOn)
            assertEquals(book.totalPages, totalPages)
            assertEquals(book.isbn10, isbn10)
            assertEquals(book.isbn13, isbn13)
            
            assertTrue(attempts.all { it.book === entity })
            assertTrue(notes.all { it.book === entity })
        }
    }
}