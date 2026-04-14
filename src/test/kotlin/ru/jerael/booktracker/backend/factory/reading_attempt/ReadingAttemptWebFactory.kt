package ru.jerael.booktracker.backend.factory.reading_attempt

import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.web.dto.reading_attempt.ReadingAttemptResponse
import ru.jerael.booktracker.backend.web.dto.reading_session.ReadingSessionResponse
import java.time.Instant
import java.util.*

object ReadingAttemptWebFactory {
    fun createReadingAttemptResponse(
        id: UUID = UUID.randomUUID(),
        status: String = BookStatus.defaultStatus().value,
        startedAt: Instant = Instant.now(),
        finishedAt: Instant? = null,
        sessions: List<ReadingSessionResponse> = emptyList()
    ): ReadingAttemptResponse {
        return ReadingAttemptResponse(id, status, startedAt, finishedAt, sessions)
    }
}
