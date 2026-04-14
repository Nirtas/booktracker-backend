package ru.jerael.booktracker.backend.factory.note

import ru.jerael.booktracker.backend.domain.model.note.NoteType
import ru.jerael.booktracker.backend.web.dto.note.NoteResponse
import java.time.Instant
import java.util.*

object NoteWebFactory {
    fun createNoteResponse(
        id: UUID = UUID.randomUUID(),
        type: String = NoteType.TEXT.name,
        textContent: String? = "text",
        fileName: String? = null,
        pageNumber: Int = 50,
        createdAt: Instant = Instant.now()
    ): NoteResponse {
        return NoteResponse(id, type, textContent, fileName, pageNumber, createdAt)
    }
}
