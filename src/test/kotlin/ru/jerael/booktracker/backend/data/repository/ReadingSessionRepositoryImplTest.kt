package ru.jerael.booktracker.backend.data.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaReadingAttemptRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaReadingSessionRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository
import ru.jerael.booktracker.backend.data.mapper.ReadingSessionDataMapper
import ru.jerael.booktracker.backend.factory.book.BookEntityFactory
import ru.jerael.booktracker.backend.factory.reading_attempt.ReadingAttemptEntityFactory
import ru.jerael.booktracker.backend.factory.reading_session.ReadingSessionDomainFactory
import ru.jerael.booktracker.backend.factory.reading_session.ReadingSessionEntityFactory
import ru.jerael.booktracker.backend.factory.user.UserEntityFactory
import java.time.Instant

@DataJpaTest
@Import(ReadingSessionRepositoryImpl::class, ReadingSessionDataMapper::class)
class ReadingSessionRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository
    
    @Autowired
    private lateinit var jpaBookRepository: JpaBookRepository
    
    @Autowired
    private lateinit var jpaReadingAttemptRepository: JpaReadingAttemptRepository
    
    @Autowired
    private lateinit var jpaReadingSessionRepository: JpaReadingSessionRepository
    
    @Autowired
    private lateinit var readingSessionRepository: ReadingSessionRepositoryImpl
    
    @Test
    fun `findAllByAttemptId should return list of reading sessions in attempt`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        val attempt = jpaReadingAttemptRepository.save(
            ReadingAttemptEntityFactory.createReadingAttemptEntity(book = book)
        )
        
        jpaReadingSessionRepository.saveAll(
            listOf(
                ReadingSessionEntityFactory.createReadingSessionEntity(
                    readingAttempt = attempt,
                    startPage = 0,
                    endPage = 10
                ),
                ReadingSessionEntityFactory.createReadingSessionEntity(
                    readingAttempt = attempt,
                    startPage = 10,
                    endPage = 25
                )
            )
        )
        
        val result = readingSessionRepository.findAllByAttemptId(attempt.id)
        
        with(result) {
            assertEquals(2, size)
            assertEquals(0, this[0].startPage)
            assertEquals(10, this[0].endPage)
        }
    }
    
    @Test
    fun `when id is null, save should insert new ReadingSession`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        val attempt = jpaReadingAttemptRepository.save(
            ReadingAttemptEntityFactory.createReadingAttemptEntity(book = book)
        )
        
        val session = ReadingSessionDomainFactory.createReadingSession(id = null, attemptId = attempt.id)
        
        val result = readingSessionRepository.save(session)
        
        with(result) {
            assertNotNull(id)
            assertEquals(attempt.id, attemptId)
        }
    }
    
    @Test
    fun `when id is present, save should update existing ReadingSession`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        val attempt = jpaReadingAttemptRepository.save(
            ReadingAttemptEntityFactory.createReadingAttemptEntity(book = book)
        )
        val savedSession = jpaReadingSessionRepository.save(
            ReadingSessionEntityFactory.createReadingSessionEntity(readingAttempt = attempt, finishedAt = null)
        )
        val updatedSession = ReadingSessionDomainFactory.createReadingSession(
            id = savedSession.id,
            attemptId = attempt.id,
            finishedAt = Instant.now()
        )
        
        val result = readingSessionRepository.save(updatedSession)
        
        with(result) {
            assertEquals(attempt.id, attemptId)
            assertEquals(updatedSession.finishedAt, finishedAt)
        }
    }
}