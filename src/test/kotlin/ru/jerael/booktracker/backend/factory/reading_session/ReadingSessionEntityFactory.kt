package ru.jerael.booktracker.backend.factory.reading_session

import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity
import ru.jerael.booktracker.backend.data.db.entity.ReadingSessionEntity
import java.time.Instant
import java.util.*

object ReadingSessionEntityFactory {
    fun createReadingSessionEntity(
        id: UUID = UUID.randomUUID(),
        readingAttempt: ReadingAttemptEntity = ReadingAttemptEntity().apply { this.id = UUID.randomUUID() },
        startPage: Int = 20,
        endPage: Int = 40,
        startedAt: Instant = Instant.now().minusSeconds(600),
        finishedAt: Instant = Instant.now()
    ): ReadingSessionEntity {
        return ReadingSessionEntity().apply {
            this.id = id; this.readingAttempt = readingAttempt; this.startPage = startPage
            this.endPage = endPage; this.startedAt = startedAt; this.finishedAt = finishedAt
        }
    }
}
