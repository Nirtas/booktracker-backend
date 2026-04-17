package ru.jerael.booktracker.backend.factory.note

import ru.jerael.booktracker.backend.domain.model.note.Note
import ru.jerael.booktracker.backend.domain.model.note.NoteType
import java.time.Instant
import java.util.*

object NoteDomainFactory {
    fun createNote(
        id: UUID? = UUID.randomUUID(),
        bookId: UUID = UUID.randomUUID(),
        type: NoteType = NoteType.TEXT,
        textContent: String? = "text",
        fileName: String? = null,
        pageNumber: Int = 50,
        createdAt: Instant = Instant.now()
    ): Note {
        return Note(id, bookId, type, textContent, fileName, pageNumber, createdAt)
    }
}
