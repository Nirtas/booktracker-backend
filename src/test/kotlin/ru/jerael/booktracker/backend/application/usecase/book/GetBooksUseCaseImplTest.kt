package ru.jerael.booktracker.backend.application.usecase.book

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult
import ru.jerael.booktracker.backend.domain.model.pagination.SortDirection
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.factory.book.BookDomainFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class GetBooksUseCaseImplTest {
    
    @MockK
    private lateinit var bookRepository: BookRepository
    
    @InjectMockKs
    private lateinit var useCase: GetBooksUseCaseImpl
    
    @Test
    fun `execute should return PageResult for books`() {
        val userId = UUID.randomUUID()
        val book1 = BookDomainFactory.createBook(userId = userId, title = "Book 1")
        val book2 = BookDomainFactory.createBook(userId = userId, title = "Book 2")
        val pageQuery = PageQuery(0, 10, "title", SortDirection.ASC)
        val pageResult = PageResult(listOf(book1, book2), 10, 0, 2, 1)
        
        every { bookRepository.findAllByUserId(pageQuery, userId) } returns pageResult
        
        val result = useCase.execute(pageQuery, userId)
        
        assertEquals(pageResult, result)
        assertEquals(2, result.content.size)
        
        verify { bookRepository.findAllByUserId(pageQuery, userId) }
    }
}