package ru.jerael.booktracker.backend.web.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.reading_attempt.ReadingAttemptDomainFactory

class ReadingAttemptWebMapperTest {
    private val readingSessionWebMapper = ReadingSessionWebMapper()
    
    private val mapper = ReadingAttemptWebMapper(readingSessionWebMapper)
    
    @Test
    fun `toResponse should map ReadingAttempt to ReadingAttemptResponse`() {
        val attempt = ReadingAttemptDomainFactory.createReadingAttempt()
        
        val response = mapper.toResponse(attempt)
        
        with(response) {
            assertEquals(attempt.id, id)
            assertEquals(attempt.status.value, status)
            assertEquals(attempt.startedAt, startedAt)
            assertEquals(attempt.finishedAt, finishedAt)
            
            val expectedSessions = attempt.sessions.map { readingSessionWebMapper.toResponse(it) }
            assertEquals(expectedSessions, sessions)
        }
    }
}