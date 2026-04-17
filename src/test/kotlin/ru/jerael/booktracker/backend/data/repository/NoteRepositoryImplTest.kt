package ru.jerael.booktracker.backend.data.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaNoteRepository
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository
import ru.jerael.booktracker.backend.data.mapper.NoteDataMapper
import ru.jerael.booktracker.backend.domain.model.note.NoteType
import ru.jerael.booktracker.backend.factory.book.BookEntityFactory
import ru.jerael.booktracker.backend.factory.note.NoteDomainFactory
import ru.jerael.booktracker.backend.factory.note.NoteEntityFactory
import ru.jerael.booktracker.backend.factory.user.UserEntityFactory

@DataJpaTest
@Import(NoteRepositoryImpl::class, NoteDataMapper::class)
class NoteRepositoryImplTest {
    
    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository
    
    @Autowired
    private lateinit var jpaBookRepository: JpaBookRepository
    
    @Autowired
    private lateinit var jpaNoteRepository: JpaNoteRepository
    
    @Autowired
    private lateinit var noteRepository: NoteRepositoryImpl
    
    
    @Test
    fun `findAllByBookId should return list of notes for book`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book1 = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        val book2 = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        
        jpaNoteRepository.saveAll(
            listOf(
                NoteEntityFactory.createNoteEntity(book = book1, type = NoteType.TEXT),
                NoteEntityFactory.createNoteEntity(book = book1, type = NoteType.IMAGE),
                NoteEntityFactory.createNoteEntity(book = book2, type = NoteType.AUDIO)
            )
        )
        
        val result = noteRepository.findAllByBookId(book1.id)
        
        with(result) {
            assertEquals(2, size)
            assertThat(this).extracting("type").containsExactlyInAnyOrder(NoteType.TEXT, NoteType.IMAGE)
        }
    }
    
    @Test
    fun `when id is null, save should insert new note`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        val note = NoteDomainFactory.createNote(id = null, bookId = book.id, textContent = "content")
        
        val result = noteRepository.save(note)
        
        with(result) {
            assertNotNull(id)
            assertEquals(book.id, bookId)
            assertEquals("content", textContent)
        }
    }
    
    @Test
    fun `when id is present, save should update existing note`() {
        val user = jpaUserRepository.save(UserEntityFactory.createUserEntity())
        val book = jpaBookRepository.save(BookEntityFactory.createBookEntity(user = user))
        val savedNote = jpaNoteRepository.save(
            NoteEntityFactory.createNoteEntity(book = book, type = NoteType.TEXT)
        )
        val updatedNote = NoteDomainFactory.createNote(id = savedNote.id, bookId = book.id, type = NoteType.IMAGE)
        
        val result = noteRepository.save(updatedNote)
        
        with(result) {
            assertEquals(savedNote.id, id)
            assertEquals(book.id, bookId)
            assertEquals(NoteType.IMAGE, type)
        }
    }
}