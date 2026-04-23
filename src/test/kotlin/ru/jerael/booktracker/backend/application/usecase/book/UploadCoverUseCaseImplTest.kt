package ru.jerael.booktracker.backend.application.usecase.book

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import ru.jerael.booktracker.backend.domain.exception.NotFoundException
import ru.jerael.booktracker.backend.domain.exception.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.UploadCoverPayload
import ru.jerael.booktracker.backend.domain.model.image.ImageFile
import ru.jerael.booktracker.backend.domain.model.image.ProcessedImage
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.service.image.ImageProcessor
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import ru.jerael.booktracker.backend.factory.image.ImageDomainFactory
import java.io.IOException
import java.util.*

@ExtendWith(MockKExtension::class)
class UploadCoverUseCaseImplTest {
    
    @MockK
    private lateinit var bookRepository: BookRepository
    
    @MockK
    private lateinit var bookCoverStorage: BookCoverStorage
    
    @MockK
    private lateinit var imageProcessor: ImageProcessor
    
    @InjectMockKs
    private lateinit var useCase: UploadCoverUseCaseImpl
    
    private val bookId: UUID = UUID.randomUUID()
    private val userId: UUID = UUID.randomUUID()
    
    private lateinit var data: UploadCoverPayload
    private lateinit var processedImage: ProcessedImage
    private lateinit var imageFile: ImageFile
    
    @BeforeEach
    fun setUp() {
        data = BookDomainFactory.createUploadCoverPayload(bookId = bookId, userId = userId)
        processedImage = ImageDomainFactory.createProcessedImage(
            contentType = data.contentType,
            content = data.content,
            size = data.size
        )
        imageFile = ImageDomainFactory.createImageFile(
            fileName = "$bookId.jpg",
            contentType = data.contentType,
            content = data.content,
            size = data.size
        )
        
        every { imageProcessor.process(any()) } returns processedImage
        every { bookCoverStorage.save(imageFile) } just Runs
        every { bookRepository.save(any(), eq(userId)) } answers { it.invocation.args[0] as Book }
    }
    
    @Test
    fun `when book does not exists, execute should throw NotFoundException`() {
        val data = BookDomainFactory.createUploadCoverPayload(bookId = bookId, userId = userId)
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.empty()
        
        assertThrows(NotFoundException::class.java) { useCase.execute(data) }
        
        verify { bookCoverStorage wasNot called }
    }
    
    @Test
    fun `when content type is not allowed, execute should throw ValidationException`() {
        val data = BookDomainFactory.createUploadCoverPayload(
            bookId = bookId,
            userId = userId,
            contentType = MediaType.APPLICATION_JSON_VALUE
        )
        val book = BookDomainFactory.createBook(id = bookId, userId = userId)
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        assertThrows(ValidationException::class.java) { useCase.execute(data) }
        
        verify { bookCoverStorage wasNot called }
    }
    
    @Test
    fun `when data is valid and old cover does not exists, execute should save cover and update file name`() {
        val book = BookDomainFactory.createBook(id = bookId, userId = userId, coverFileName = null)
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        val result = useCase.execute(data)
        
        assertNotNull(result)
        assertEquals(bookId, result.id)
        assertEquals(userId, result.userId)
        
        val bookSlot = slot<Book>()
        verify { bookRepository.save(capture(bookSlot), eq(userId)) }
        
        assertEquals(imageFile.fileName, bookSlot.captured.coverFileName)
        
        verify { bookCoverStorage.save(imageFile) }
        verify { bookRepository.save(any(), eq(userId)) }
        verify(exactly = 0) { bookCoverStorage.delete(any()) }
    }
    
    @Test
    fun `when old cover exists, execute should delete old cover`() {
        val book = BookDomainFactory.createBook(id = bookId, userId = userId, coverFileName = "old_cover.jpg")
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        
        val result = useCase.execute(data)
        
        assertEquals(imageFile.fileName, result.coverFileName)
        
        verify { bookCoverStorage.delete(book.coverFileName) }
    }
    
    @Test
    fun `when delete old cover fails, execute should return updated book`() {
        val book = BookDomainFactory.createBook(id = bookId, userId = userId, coverFileName = "old_cover.jpg")
        
        every { bookRepository.findByIdAndUserId(bookId, userId) } returns Optional.of(book)
        every { bookCoverStorage.delete(any()) } throws IOException("Error")
        
        assertDoesNotThrow { useCase.execute(data) }
        
        verify { bookRepository.save(any(), eq(userId)) }
    }
}