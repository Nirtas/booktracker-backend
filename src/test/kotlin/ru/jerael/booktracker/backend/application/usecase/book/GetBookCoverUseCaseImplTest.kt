package ru.jerael.booktracker.backend.application.usecase.book

import io.mockk.called
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
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import ru.jerael.booktracker.backend.factory.image.ImageDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class GetBookCoverUseCaseImplTest {
    
    @MockK
    private lateinit var bookRepository: BookRepository
    
    @MockK
    private lateinit var bookCoverStorage: BookCoverStorage
    
    @InjectMockKs
    private lateinit var useCase: GetBookCoverUseCaseImpl
    
    private val bookId: UUID = UUID.randomUUID()
    private val userId: UUID = UUID.randomUUID()
    
    @Test
    fun `when cover exists, execute should return ImageFile`() {
        val coverFileName = "cover.jpg"
        val book = BookDomainFactory.createBook(
            id = bookId,
            userId = userId,
            coverFileName = coverFileName
        )
        val imageFile = ImageDomainFactory.createImageFile(fileName = coverFileName)
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        every { bookCoverStorage.download(coverFileName) } returns imageFile
        
        val result = useCase.execute(bookId, userId)
        
        assertEquals(imageFile, result)
        
        verify { bookRepository.findByIdAndUserId(bookId, userId) }
        verify { bookCoverStorage.download(coverFileName) }
    }
    
    @Test
    fun `when book does not exists, execute should throw NotFoundException`() {
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.empty()
        
        assertThrows(NotFoundException::class.java) { useCase.execute(bookId, userId) }
    }
    
    @Test
    fun `when cover does not exists, execute should throw NotFoundException`() {
        val book = BookDomainFactory.createBook(
            id = bookId,
            userId = userId,
            coverFileName = null
        )
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        assertThrows(NotFoundException::class.java) { useCase.execute(bookId, userId) }
        
        verify { bookRepository.findByIdAndUserId(bookId, userId) }
        verify { bookCoverStorage wasNot called }
    }
}