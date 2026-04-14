package ru.jerael.booktracker.backend.factory.reading_session

import ru.jerael.booktracker.backend.web.dto.reading_session.ReadingSessionResponse
import java.time.Instant
import java.util.*

object ReadingSessionWebFactory {
    fun createReadingSessionResponse(
        id: UUID = UUID.randomUUID(),
        startPage: Int = 20,
        endPage: Int = 40,
        startedAt: Instant = Instant.now().minusSeconds(600),
        finishedAt: Instant = Instant.now()
    ): ReadingSessionResponse {
        return ReadingSessionResponse(id, startPage, endPage, startedAt, finishedAt)
    }
}
