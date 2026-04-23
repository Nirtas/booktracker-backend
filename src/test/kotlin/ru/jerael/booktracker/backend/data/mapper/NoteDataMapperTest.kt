package ru.jerael.booktracker.backend.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.factory.note.NoteDomainFactory
import ru.jerael.booktracker.backend.factory.note.NoteEntityFactory
import java.util.*

class NoteDataMapperTest {
    private val mapper = NoteDataMapper()
    
    @Test
    fun `toEntity should map Note to NoteEntity`() {
        val note = NoteDomainFactory.createNote()
        
        val entity = mapper.toEntity(note)
        
        with(entity) {
            assertEquals(note.id, id)
            assertEquals(note.bookId, book.id)
            assertEquals(note.type, type)
            assertEquals(note.textContent, textContent)
            assertEquals(note.fileName, fileName)
            assertEquals(note.pageNumber, pageNumber)
            assertEquals(note.createdAt, createdAt)
        }
    }
    
    @Test
    fun `toDomain should map NoteEntity to Note`() {
        val entity = NoteEntityFactory.createNoteEntity(id = UUID.randomUUID())
        
        val note = mapper.toDomain(entity)
        
        with(note) {
            assertEquals(entity.id, id)
            assertEquals(entity.book.id, bookId)
            assertEquals(entity.type, type)
            assertEquals(entity.textContent, textContent)
            assertEquals(entity.fileName, fileName)
            assertEquals(entity.pageNumber, pageNumber)
            assertEquals(entity.createdAt, createdAt)
        }
    }
}