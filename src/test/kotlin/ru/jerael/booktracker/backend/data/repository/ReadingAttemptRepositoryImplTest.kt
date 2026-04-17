package ru.jerael.booktracker.backend.data.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaReadingAttemptRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository
import ru.jerael.booktracker.backend.data.mapper.ReadingAttemptDataMapper
import ru.jerael.booktracker.backend.data.mapper.ReadingSessionDataMapper
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.factory.book.BookEntityFactory
import ru.jerael.booktracker.backend.factory.reading_attempt.ReadingAttemptDomainFactory
import ru.jerael.booktracker.backend.factory.reading_attempt.ReadingAttemptEntityFactory
import ru.jerael.booktracker.backend.factory.user.UserEntityFactory

@DataJpaTest
@Import(ReadingAttemptRepositoryImpl::class, ReadingAttemptDataMapper::class, ReadingSessionDataMapper::class)
class ReadingAttemptRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository
    
    @Autowired
    private lateinit var jpaBookRepository: JpaBookRepository
    
    @Autowired
    private lateinit var jpaReadingAttemptRepository: JpaReadingAttemptRepository
    
    @Autowired
    private lateinit var readingAttemptRepository: ReadingAttemptRepositoryImpl
    
    @Test
    fun `findAllByBookId should return list of reading attempts of book`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book1 = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        val book2 = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        
        jpaReadingAttemptRepository.saveAll(
            listOf(
                ReadingAttemptEntityFactory.createReadingAttemptEntity(book = book1, status = BookStatus.READING),
                ReadingAttemptEntityFactory.createReadingAttemptEntity(book = book1, status = BookStatus.COMPLETED),
                ReadingAttemptEntityFactory.createReadingAttemptEntity(book = book2, status = BookStatus.WANT_TO_READ)
            )
        )
        
        val result = readingAttemptRepository.findAllByBookId(book1.id)
        
        assertEquals(2, result.size)
    }
    
    @Test
    fun `findAllByBookIdAndStatus should return list of reading attempts of book with status`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        
        jpaReadingAttemptRepository.saveAll(
            listOf(
                ReadingAttemptEntityFactory.createReadingAttemptEntity(book = book, status = BookStatus.READING),
                ReadingAttemptEntityFactory.createReadingAttemptEntity(book = book, status = BookStatus.COMPLETED)
            )
        )
        
        val result = readingAttemptRepository.findAllByBookIdAndStatus(book.id, BookStatus.READING)
        
        assertEquals(1, result.size)
        assertEquals(BookStatus.READING, result[0].status)
    }
    
    @Test
    fun `when id is null, save should insert new ReadingAttempt`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        val attempt = ReadingAttemptDomainFactory.createReadingAttempt(id = null, bookId = book.id)
        
        val result = readingAttemptRepository.save(attempt)
        
        with(result) {
            assertNotNull(id)
            assertEquals(book.id, bookId)
            assertEquals(attempt.status, status)
            assertNotNull(startedAt)
        }
    }
    
    @Test
    fun `when id is present, save should update existing ReadingAttempt`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        val savedAttempt = jpaReadingAttemptRepository.save(
            ReadingAttemptEntityFactory.createReadingAttemptEntity(
                book = book,
                status = BookStatus.READING
            )
        )
        val updatedAttempt = ReadingAttemptDomainFactory.createReadingAttempt(
            id = savedAttempt.id,
            bookId = book.id,
            status = BookStatus.COMPLETED
        )
        
        val result = readingAttemptRepository.save(updatedAttempt)
        
        with(result) {
            assertEquals(savedAttempt.id, id)
            assertEquals(BookStatus.COMPLETED, status)
        }
    }
}