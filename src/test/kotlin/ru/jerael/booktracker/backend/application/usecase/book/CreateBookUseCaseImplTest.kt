package ru.jerael.booktracker.backend.application.usecase.book

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.model.Genre
import ru.jerael.booktracker.backend.domain.model.author.Author
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.language.Language
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher
import ru.jerael.booktracker.backend.domain.repository.*
import ru.jerael.booktracker.backend.domain.validator.BookValidator
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
class CreateBookUseCaseImplTest {
    
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
    private lateinit var useCase: CreateBookUseCaseImpl
    
    @BeforeEach
    fun setUp() {
        every { authorRepository.findByFullName(any()) } returns Optional.empty()
        every { authorRepository.save(any()) } answers { it.invocation.args[0] as Author }
        
        every { publisherRepository.findByName(any()) } returns Optional.empty()
        every { publisherRepository.save(any()) } answers { it.invocation.args[0] as Publisher }
        
        every { languageRepository.findByCode("en") } returns Optional.of(Language("en", "English"))
    }
    
    @Test
    fun `when all genres found, execute should create book with all genres`() {
        val genreIds = setOf(1, 2)
        val foundGenres = setOf(
            Genre(1, "Genre 1"),
            Genre(2, "Genre 2")
        )
        val userId = UUID.randomUUID()
        val bookId = UUID.randomUUID()
        val bookCreation = BookDomainFactory.createBookCreation(
            userId = userId,
            genreIds = genreIds,
            languageCode = "en"
        )
        
        every { genreRepository.findAllById(genreIds) } returns foundGenres
        every { bookRepository.save(any(), eq(userId)) } answers {
            val book = it.invocation.args[0] as Book
            BookDomainFactory.createBook(
                id = bookId,
                userId = userId,
                genres = book.genres
            )
        }
        
        with(useCase.execute(bookCreation)) {
            assertEquals(bookId, id)
            assertNotNull(createdAt)
            assertEquals(bookCreation.status, status())
            assertEquals(genres, this.genres)
        }
        
        verify { genreRepository.findAllById(genreIds) }
        
        val bookSlot = slot<Book>()
        verify { bookRepository.save(capture(bookSlot), eq(userId)) }
        
        with(bookSlot.captured) {
            assertNull(id)
            assertThat(createdAt).isBefore(Instant.now().plusSeconds(2))
        }
    }
    
    @Test
    fun `when one or more genres not found, execute should throw NotFoundException`() {
        val genreIds = setOf(1, 2)
        val foundGenres = setOf(Genre(1, "Genre 1"))
        val bookCreation = BookDomainFactory.createBookCreation(genreIds = genreIds, languageCode = "en")
        
        every { genreRepository.findAllById(genreIds) } returns foundGenres
        
        assertThrows(NotFoundException::class.java) { useCase.execute(bookCreation) }
        
        verify(exactly = 0) { bookRepository.save(any(), eq(bookCreation.userId)) }
    }
}