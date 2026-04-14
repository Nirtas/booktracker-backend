package ru.jerael.booktracker.backend.factory.reading_attempt

import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt
import ru.jerael.booktracker.backend.domain.model.reading_session.ReadingSession
import java.time.Instant
import java.util.*

object ReadingAttemptDomainFactory {
    fun createReadingAttempt(
        id: UUID = UUID.randomUUID(),
        bookId: UUID = UUID.randomUUID(),
        status: BookStatus = BookStatus.defaultStatus(),
        startedAt: Instant = Instant.now(),
        finishedAt: Instant? = null,
        sessions: List<ReadingSession> = emptyList()
    ): ReadingAttempt {
        return ReadingAttempt(id, bookId, status, startedAt, finishedAt, sessions)
    }
}
