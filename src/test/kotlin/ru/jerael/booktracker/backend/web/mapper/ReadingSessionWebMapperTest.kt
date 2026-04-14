package ru.jerael.booktracker.backend.web.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.reading_session.ReadingSessionDomainFactory

class ReadingSessionWebMapperTest {
    private val mapper = ReadingSessionWebMapper()
    
    @Test
    fun `toResponse should map ReadingSession to ReadingSessionResponse`() {
        val session = ReadingSessionDomainFactory.createReadingSession()
        
        val response = mapper.toResponse(session)
        
        with(response) {
            assertEquals(session.id, id)
            assertEquals(session.startPage, startPage)
            assertEquals(session.endPage, endPage)
            assertEquals(session.startedAt, startedAt)
            assertEquals(session.finishedAt, finishedAt)
        }
    }
}