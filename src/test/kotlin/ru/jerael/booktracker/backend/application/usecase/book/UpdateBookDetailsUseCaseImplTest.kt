package ru.jerael.booktracker.backend.application.usecase.book

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.exception.UnprocessableContentException
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.model.author.Author
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.language.Language
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher
import ru.jerael.booktracker.backend.domain.repository.*
import ru.jerael.booktracker.backend.domain.validator.BookValidator
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import ru.jerael.booktracker.backend.factory.reading_attempt.ReadingAttemptDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class UpdateBookDetailsUseCaseImplTest {
    
    @MockK
    private lateinit var bookRepository: BookRepository
    
    @MockK
    private lateinit var genreRepository: GenreRepository
    
    @MockK
    private lateinit var authorRepository: AuthorRepository
    
    @MockK
    private lateinit var publisherRepository: PublisherRepository
    
    @MockK
    private lateinit var languageRepository: LanguageRepository
    
    @MockK(relaxed = true)
    private lateinit var bookValidator: BookValidator
    
    @InjectMockKs
    private lateinit var useCase: UpdateBookDetailsUseCaseImpl
    
    private val bookId: UUID = UUID.randomUUID()
    private val userId: UUID = UUID.randomUUID()
    
    @BeforeEach
    fun setUp() {
        every { genreRepository.findAllById(any()) } answers {
            val genreIds = it.invocation.args[0] as Set<Int>
            genreIds.map { id -> Genre(id, "Genre $id") }.toSet()
        }
        every { authorRepository.findByFullName(any()) } answers {
            val name = it.invocation.args[0] as String
            Optional.of(Author(UUID.randomUUID(), name))
        }
        every { publisherRepository.findByName(any()) } answers {
            val name = it.invocation.args[0] as String
            Optional.of(Publisher(UUID.randomUUID(), name))
        }
        every { languageRepository.findByCode(any()) } answers {
            val code = it.invocation.args[0] as String
            Optional.of(Language(code, "Language"))
        }
        every { bookRepository.save(any(), userId) } answers { it.invocation.args[0] as Book }
    }
    
    @Test
    fun `execute should update only provided fields`() {
        val book = BookDomainFactory.createBook(
            id = bookId,
            userId = userId,
            title = "Old Title",
            description = "Description"
        )
        val data = BookDomainFactory.createBookDetailsUpdate(
            bookId = bookId,
            userId = userId,
            title = "New Title"
        )
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        with(useCase.execute(data)) {
            assertEquals(data.title, title)
            assertEquals(book.description, description)
            assertEquals(book.authors, authors)
        }
        
        verify { bookRepository.save(any(), eq(userId)) }
    }
    
    @Test
    fun `when new author provided, execute should save and update book`() {
        val book = BookDomainFactory.createBook(id = bookId, userId = userId)
        val newAuthorName = "New Author"
        val data = BookDomainFactory.createBookDetailsUpdate(
            bookId = bookId,
            userId = userId,
            authorNames = setOf(newAuthorName)
        )
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        every { authorRepository.findByFullName(newAuthorName) } returns Optional.empty()
        every { authorRepository.save(any()) } answers { it.invocation.args[0] as Author }
        
        with(useCase.execute(data)) {
            assertEquals(1, authors.size)
            assertEquals(newAuthorName, authors.last().fullName)
        }
        
        verify { authorRepository.save(match { it.fullName == newAuthorName }) }
    }
    
    @Test
    fun `when one or more genres not found, execute should throw NotFoundException`() {
        val book = BookDomainFactory.createBook(id = bookId, userId = userId)
        val genreIds = setOf(1, 5555)
        val data = BookDomainFactory.createBookDetailsUpdate(
            bookId = bookId,
            userId = userId,
            genreIds = genreIds
        )
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        every { genreRepository.findAllById(genreIds) } returns setOf(Genre(1, "Genre 1"))
        
        val exception = assertThrows(NotFoundException::class.java) { useCase.execute(data) }
        
        assertThat(exception.message).contains("5555")
    }
    
    @Test
    fun `when status changed from WANT_TO_READ to READING, execute should update current attempt`() {
        val book = BookDomainFactory.createBook(
            id = bookId,
            userId = userId,
            attempts = listOf(
                ReadingAttemptDomainFactory.createReadingAttempt(status = BookStatus.WANT_TO_READ)
            )
        )
        val data = BookDomainFactory.createBookDetailsUpdate(
            bookId = bookId,
            userId = userId,
            status = BookStatus.READING
        )
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        with(useCase.execute(data)) {
            assertEquals(1, attempts.size)
            assertEquals(BookStatus.READING, status())
        }
    }
    
    @Test
    fun `when status changed from COMPLETED to READING, execute should create new attempt`() {
        val book = BookDomainFactory.createBook(
            id = bookId,
            userId = userId,
            attempts = listOf(
                ReadingAttemptDomainFactory.createReadingAttempt(status = BookStatus.COMPLETED)
            )
        )
        val data = BookDomainFactory.createBookDetailsUpdate(
            bookId = bookId,
            userId = userId,
            status = BookStatus.READING
        )
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        with(useCase.execute(data)) {
            assertEquals(2, attempts.size)
            assertEquals(BookStatus.READING, status())
            assertEquals(BookStatus.COMPLETED, attempts.first().status)
        }
    }
    
    @Test
    fun `when status changed from COMPLETED to DROPPED, execute should throw UnprocessableContentException`() {
        val book = BookDomainFactory.createBook(
            id = bookId,
            userId = userId,
            attempts = listOf(
                ReadingAttemptDomainFactory.createReadingAttempt(status = BookStatus.COMPLETED)
            )
        )
        val data = BookDomainFactory.createBookDetailsUpdate(
            bookId = bookId,
            userId = userId,
            status = BookStatus.DROPPED
        )
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        assertThrows(UnprocessableContentException::class.java) { useCase.execute(data) }
    }
    
    @Test
    fun `when status changed to COMPLETED and no sessions exist, execute should create auto session`() {
        val book = BookDomainFactory.createBook(
            id = bookId,
            userId = userId,
            attempts = listOf(
                ReadingAttemptDomainFactory.createReadingAttempt(
                    status = BookStatus.WANT_TO_READ,
                    sessions = emptyList()
                )
            )
        )
        val data = BookDomainFactory.createBookDetailsUpdate(
            bookId = bookId,
            userId = userId,
            status = BookStatus.COMPLETED
        )
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        with(useCase.execute(data)) {
            assertEquals(1, attempts.last().sessions.size)
            assertEquals(0, attempts.last().sessions.last().startPage)
            assertEquals(book.totalPages, attempts.last().sessions.last().endPage)
            assertEquals(BookStatus.COMPLETED, status())
        }
    }
}