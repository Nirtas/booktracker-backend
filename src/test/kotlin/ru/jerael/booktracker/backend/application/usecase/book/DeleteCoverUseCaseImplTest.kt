package ru.jerael.booktracker.backend.application.usecase.book

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class DeleteCoverUseCaseImplTest {
    
    @MockK
    private lateinit var bookRepository: BookRepository
    
    @MockK
    private lateinit var bookCoverStorage: BookCoverStorage
    
    @InjectMockKs
    private lateinit var useCase: DeleteCoverUseCaseImpl
    
    private val bookId: UUID = UUID.randomUUID()
    private val userId: UUID = UUID.randomUUID()
    
    @BeforeEach
    fun setUp() {
        every { bookRepository.save(any(), any()) } returns mockk()
        every { bookCoverStorage.delete(any()) } just Runs
    }
    
    @Test
    fun `when book does not exists, execute should throw NotFoundException`() {
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.empty()
        
        assertThrows(NotFoundException::class.java) { useCase.execute(bookId, userId) }
        
        verify { bookCoverStorage wasNot called }
    }
    
    @Test
    fun `when cover exists, execute should delete from storage and save book with null cover`() {
        val coverFileName = "cover.jpg"
        val book = BookDomainFactory.createBook(
            id = bookId,
            userId = userId,
            coverFileName = coverFileName
        )
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        useCase.execute(bookId, userId)
        
        verify { bookRepository.save(match { it.id == bookId && it.coverFileName == null }, userId) }
        verify { bookCoverStorage.delete(coverFileName) }
    }
    
    @Test
    fun `when cover does not exists, execute should exit without update`() {
        val book = BookDomainFactory.createBook(
            id = bookId,
            userId = userId,
            coverFileName = null
        )
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        useCase.execute(bookId, userId)
        
        verify(exactly = 0) { bookRepository.save(any(), userId) }
        verify(exactly = 0) { bookCoverStorage.delete(any()) }
    }
}