package ru.jerael.booktracker.backend.web.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.note.NoteDomainFactory

class NoteWebMapperTest {
    private val mapper = NoteWebMapper()
    
    @Test
    fun `toResponse should map Note to NoteResponse`() {
        val note = NoteDomainFactory.createNote()
        
        val response = mapper.toResponse(note)
        
        with(response) {
            assertEquals(note.id, id)
            assertEquals(note.type.name, type)
            assertEquals(note.textContent, textContent)
            assertEquals(note.fileName, fileName)
            assertEquals(note.pageNumber, pageNumber)
            assertEquals(note.createdAt, createdAt)
        }
    }
}