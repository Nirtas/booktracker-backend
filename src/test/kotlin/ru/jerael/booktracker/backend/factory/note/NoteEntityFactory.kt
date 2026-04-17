package ru.jerael.booktracker.backend.factory.note

import ru.jerael.booktracker.backend.data.db.entity.BookEntity
import ru.jerael.booktracker.backend.data.db.entity.NoteEntity
import ru.jerael.booktracker.backend.domain.model.note.NoteType
import java.time.Instant
import java.util.*

object NoteEntityFactory {
    fun createNoteEntity(
        id: UUID? = null,
        book: BookEntity? = BookEntity().apply { this.id = UUID.randomUUID() },
        type: NoteType = NoteType.TEXT,
        textContent: String? = "text",
        fileName: String? = null,
        pageNumber: Int = 50,
        createdAt: Instant = Instant.now()
    ): NoteEntity {
        return NoteEntity().apply {
            this.id = id; this.book = book; this.type = type; this.textContent = textContent
            this.fileName = fileName; this.pageNumber = pageNumber; this.createdAt = createdAt
        }
    }
}
