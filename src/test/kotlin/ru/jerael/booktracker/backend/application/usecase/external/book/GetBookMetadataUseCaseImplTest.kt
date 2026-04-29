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
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class GetBookMetadataUseCaseImplTest {
    
    @MockK
    private lateinit var bookMetadataProvider: BookMetadataProvider
    
    @InjectMockKs
    private lateinit var useCase: GetBookMetadataUseCaseImpl
    
    @Test
    fun `when book metadata exists, execute should return BookMetadata`() {
        val query = BookDomainFactory.createBookSearchQuery(isbn = "1234567890")
        val bookMetadata = BookDomainFactory.createBookMetadata(isbn10 = query.isbn)
        
        every { bookMetadataProvider.findBook(query) } returns Optional.of(bookMetadata)
        
        val result = useCase.execute(query)
        
        with(result) {
            assertThat(title).isEqualTo(bookMetadata.title)
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