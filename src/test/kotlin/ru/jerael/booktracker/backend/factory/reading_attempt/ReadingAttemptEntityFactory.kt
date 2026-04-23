package ru.jerael.booktracker.backend.factory.reading_attempt

import ru.jerael.booktracker.backend.data.db.entity.BookEntity
import ru.jerael.booktracker.backend.data.db.entity.ReadingAttemptEntity
import ru.jerael.booktracker.backend.data.db.entity.ReadingSessionEntity
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import java.time.Instant
import java.util.*

object ReadingAttemptEntityFactory {
    fun createReadingAttemptEntity(
        id: UUID? = null,
        book: BookEntity = BookEntity().apply { this.id = UUID.randomUUID() },
        status: BookStatus = BookStatus.defaultStatus(),
        startedAt: Instant = Instant.now(),
        finishedAt: Instant? = null,
        sessions: List<ReadingSessionEntity> = emptyList()
    ): ReadingAttemptEntity {
        return ReadingAttemptEntity().apply {
            this.id = id; this.book = book; this.status = status; this.startedAt = startedAt
            this.finishedAt = finishedAt; this.sessions = sessions
        }
    }
}
