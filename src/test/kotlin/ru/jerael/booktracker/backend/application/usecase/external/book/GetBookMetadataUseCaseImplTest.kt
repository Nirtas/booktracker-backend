package ru.jerael.booktracker.backend.application.usecase.external.book

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.gateway.book.BookMetadataProvider
import ru.jerael.booktracker.backend.domain.repository.AuthorRepository
import ru.jerael.booktracker.backend.domain.repository.GenreRepository
import ru.jerael.booktracker.backend.domain.repository.LanguageRepository
import ru.jerael.booktracker.backend.domain.repository.PublisherRepository
import ru.jerael.booktracker.backend.factory.author.AuthorDomainFactory
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import ru.jerael.booktracker.backend.factory.genre.GenreDomainFactory
import ru.jerael.booktracker.backend.factory.language.LanguageDomainFactory
import ru.jerael.booktracker.backend.factory.publisher.PublisherDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class GetBookMetadataUseCaseImplTest {
    
    @MockK
    private lateinit var bookMetadataProvider: BookMetadataProvider
    
    @MockK
    private lateinit var genreRepository: GenreRepository
    
    @MockK
    private lateinit var authorRepository: AuthorRepository
    
    @MockK
    private lateinit var publisherRepository: PublisherRepository
    
    @MockK
    private lateinit var languageRepository: LanguageRepository
    
    @InjectMockKs
    private lateinit var useCase: GetBookMetadataUseCaseImpl
    
    @Test
    fun `when book metadata exists, execute should return BookMetadata`() {
        val query = BookDomainFactory.createBookSearchQuery(isbn = "1234567890")
        
        val genre = GenreDomainFactory.createGenre(id = null, name = "genre 1")
        val author = AuthorDomainFactory.createAuthor(id = null, fullName = "author a")
        val publisher = PublisherDomainFactory.createPublisher(id = null, name = "publisher a")
        val language = LanguageDomainFactory.createLanguage(code = "en", name = null)
        
        val bookMetadata = BookDomainFactory.createBookMetadata(
            isbn10 = query.isbn,
            genres = setOf(genre),
            authors = setOf(author),
            publisher = publisher,
            language = language
        )
        
        val dbGenre = GenreDomainFactory.createGenre(id = 1, "Genre 1")
        val dbAuthor = AuthorDomainFactory.createAuthor(fullName = "Author A")
        val dbPublisher = PublisherDomainFactory.createPublisher(name = "Publisher A")
        val dbLanguage = LanguageDomainFactory.createLanguage(code = "en", name = "English")
        
        every { bookMetadataProvider.findBook(query) } returns Optional.of(bookMetadata)
        every { genreRepository.findAllByNames(any()) } returns setOf(dbGenre)
        every { authorRepository.findAllByNames(any()) } returns setOf(dbAuthor)
        every { publisherRepository.findByName(any()) } returns Optional.of(dbPublisher)
        every { languageRepository.findByCode(any()) } returns Optional.of(dbLanguage)
        
        val result = useCase.execute(query)
        
        with(result) {
            assertThat(title).isEqualTo(bookMetadata.title)
            assertThat(genres).hasSize(1)
            assertThat(genres.first().id).isEqualTo(1)
            assertThat(authors).hasSize(1)
            assertThat(authors.first().id).isEqualTo(dbAuthor.id)
            assertThat(this.publisher).isEqualTo(dbPublisher)
            assertThat(this.language).isEqualTo(dbLanguage)
            assertThat(isbn10).isEqualTo(query.isbn)
        }
    }
    
    @Test
    fun `when book metadata does not exists, execute should throw NotFoundException`() {
        val query = BookDomainFactory.createBookSearchQuery(isbn = "1234567890")
        
        every { bookMetadataProvider.findBook(any()) } returns Optional.empty()
        
        assertThrows(NotFoundException::class.java) { useCase.execute(query) }
    }
}