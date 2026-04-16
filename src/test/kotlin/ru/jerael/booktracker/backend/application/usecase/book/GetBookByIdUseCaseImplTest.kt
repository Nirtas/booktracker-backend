package ru.jerael.booktracker.backend.application.usecase.book

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class GetBookByIdUseCaseImplTest {
    
    @MockK
    private lateinit var bookRepository: BookRepository
    
    @InjectMockKs
    private lateinit var useCase: GetBookByIdUseCaseImpl
    
    private val bookId: UUID = UUID.randomUUID()
    private val userId: UUID = UUID.randomUUID()
    
    @Test
    fun `when book exists, execute should return book`() {
        val book = BookDomainFactory.createBook(id = bookId, userId = userId)
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        val result = useCase.execute(bookId, userId)
        
        assertEquals(book, result)
        
        verify { bookRepository.findByIdAndUserId(bookId, userId) }
    }
    
    @Test
    fun `when book does not exists, execute should throw NotFoundException`() {
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.empty()
        
        assertThrows(NotFoundException::class.java) { useCase.execute(bookId, userId) }
    }
}