package ru.jerael.booktracker.backend.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.reading_attempt.ReadingAttemptDomainFactory
import ru.jerael.booktracker.backend.factory.reading_attempt.ReadingAttemptEntityFactory

class ReadingAttemptDataMapperTest {
    private val readingSessionDataMapper = ReadingSessionDataMapper()
    
    private val mapper = ReadingAttemptDataMapper(readingSessionDataMapper)
    
    @Test
    fun `toEntity should map ReadingAttempt to ReadingAttemptEntity`() {
        val attempt = ReadingAttemptDomainFactory.createReadingAttempt()
        
        val entity = mapper.toEntity(attempt)
        
        with(entity) {
            assertEquals(attempt.id, id)
            assertEquals(attempt.bookId, book.id)
            assertEquals(attempt.status, status)
            assertEquals(attempt.startedAt, startedAt)
            assertEquals(attempt.finishedAt, finishedAt)
            assertTrue(sessions.all { it.readingAttempt === entity })
        }
    }
    
    @Test
    fun `toDomain should map ReadingAttemptEntity to ReadingAttempt`() {
        val entity = ReadingAttemptEntityFactory.createReadingAttemptEntity()
        
        val attempt = mapper.toDomain(entity)
        
        with(attempt) {
            assertEquals(entity.id, id)
            assertEquals(entity.book.id, bookId)
            assertEquals(entity.status, status)
            assertEquals(entity.startedAt, startedAt)
            assertEquals(entity.finishedAt, finishedAt)
            
            val expectedSessions = entity.sessions.map { readingSessionDataMapper.toDomain(it) }
            assertEquals(expectedSessions, sessions)
        }
    }
}