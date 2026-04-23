package ru.jerael.booktracker.backend.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.reading_session.ReadingSessionDomainFactory
import ru.jerael.booktracker.backend.factory.reading_session.ReadingSessionEntityFactory
import java.util.*

class ReadingSessionDataMapperTest {
    private val mapper = ReadingSessionDataMapper()
    
    @Test
    fun `toEntity should map ReadingSession to ReadingSessionEntity`() {
        val session = ReadingSessionDomainFactory.createReadingSession()
        
        val entity = mapper.toEntity(session)
        
        with(entity) {
            assertEquals(session.id, id)
            assertEquals(session.attemptId, readingAttempt.id)
            assertEquals(session.startPage, startPage)
            assertEquals(session.endPage, endPage)
            assertEquals(session.startedAt, startedAt)
            assertEquals(session.finishedAt, finishedAt)
        }
    }
    
    @Test
    fun `toDomain should map ReadingSessionEntity to ReadingSession`() {
        val entity = ReadingSessionEntityFactory.createReadingSessionEntity(id = UUID.randomUUID())
        
        val session = mapper.toDomain(entity)
        
        with(session) {
            assertEquals(entity.id, id)
            assertEquals(entity.readingAttempt.id, attemptId)
            assertEquals(entity.startPage, startPage)
            assertEquals(entity.endPage, endPage)
            assertEquals(entity.startedAt, startedAt)
            assertEquals(entity.finishedAt, finishedAt)
        }
    }
}