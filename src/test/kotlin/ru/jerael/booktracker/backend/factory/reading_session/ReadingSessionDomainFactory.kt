package ru.jerael.booktracker.backend.factory.reading_session

import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession
import java.time.Instant
import java.util.*

object ReadingSessionDomainFactory {
    fun createReadingSession(
        id: UUID = UUID.randomUUID(),
        attemptId: UUID = UUID.randomUUID(),
        startPage: Int = 20,
        endPage: Int = 40,
        startedAt: Instant = Instant.now().minusSeconds(600),
        finishedAt: Instant = Instant.now()
    ): ReadingSession {
        return ReadingSession(id, attemptId, startPage, endPage, startedAt, finishedAt)
    }
}
