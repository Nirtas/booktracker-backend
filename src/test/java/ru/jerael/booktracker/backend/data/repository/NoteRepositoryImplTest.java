package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.NoteEntity;
import ru.jerael.booktracker.backend.data.db.entity.UserEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository;
import ru.jerael.booktracker.backend.data.db.repository.JpaNoteRepository;
import ru.jerael.booktracker.backend.data.db.repository.JpaUserRepository;
import ru.jerael.booktracker.backend.data.mapper.NoteDataMapper;
import ru.jerael.booktracker.backend.domain.model.note.Note;
import ru.jerael.booktracker.backend.domain.model.note.NoteType;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import({NoteRepositoryImpl.class, NoteDataMapper.class})
class NoteRepositoryImplTest {

    @Autowired
    private NoteRepositoryImpl noteRepository;

    @Autowired
    private JpaNoteRepository jpaNoteRepository;

    @Autowired
    private JpaBookRepository jpaBookRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    private final NoteType type = NoteType.TEXT;
    private final String textContent = "text content";
    private final String fileName = "file_name.txt";
    private final int pageNumber = 123;
    private final Instant createdAt = Instant.ofEpochMilli(1773826150059L);

    @Test
    void findAllByBookId_ShouldReturnListOfNotesForBookWithBookId() {
        UUID userId = saveUser();
        BookEntity book1 = saveBook(userId);
        BookEntity book2 = saveBook(userId);

        NoteEntity entity1 = buildNoteEntity(book1, type);
        jpaNoteRepository.save(entity1);

        NoteEntity entity2 = buildNoteEntity(book1, NoteType.IMAGE);
        jpaNoteRepository.save(entity2);

        NoteEntity entity3 = buildNoteEntity(book2, NoteType.AUDIO);
        jpaNoteRepository.save(entity3);

        List<Note> notes = noteRepository.findAllByBookId(book1.getId());

        assertEquals(2, notes.size());
        assertEquals(NoteType.TEXT, notes.get(0).type());
        assertEquals(NoteType.IMAGE, notes.get(1).type());
    }

    @Test
    void save_WhenIdIsNull_ShouldInsertNewNote() {
        UUID userId = saveUser();
        BookEntity book = saveBook(userId);
        Note note = buildNote(null, book.getId(), type);

        Note result = noteRepository.save(note);

        assertNotNull(result.id());
        assertEquals(book.getId(), result.bookId());
        assertEquals(type, result.type());
        assertEquals(textContent, result.textContent());
        assertEquals(fileName, result.fileName());
        assertEquals(pageNumber, result.pageNumber());
        assertEquals(createdAt, result.createdAt());
    }

    @Test
    void save_WhenIdIsPresent_ShouldUpdateExistingNote() {
        UUID userId = saveUser();
        BookEntity book = saveBook(userId);
        Note note = buildNote(null, book.getId(), type);

        UUID noteId = noteRepository.save(note).id();

        Note newNote = buildNote(noteId, book.getId(), NoteType.VIDEO);

        Note result = noteRepository.save(newNote);

        assertEquals(noteId, result.id());
        assertEquals(NoteType.VIDEO, result.type());
    }

    private BookEntity saveBook(UUID userId) {
        BookEntity entity = new BookEntity();
        entity.setUserId(userId);
        entity.setTitle("title");
        entity.setCoverFileName(null);
        entity.setCreatedAt(Instant.now());
        entity.setGenres(Collections.emptySet());
        return jpaBookRepository.save(entity);
    }

    private UUID saveUser() {
        UserEntity entity = new UserEntity();
        entity.setEmail("test@example.com");
        entity.setPasswordHash("password hash");
        entity.setVerified(true);
        entity.setCreatedAt(Instant.now());
        return jpaUserRepository.save(entity).getId();
    }

    private NoteEntity buildNoteEntity(BookEntity book, NoteType type) {
        NoteEntity entity = new NoteEntity();
        entity.setBook(book);
        entity.setType(type);
        entity.setTextContent(textContent);
        entity.setFileName(fileName);
        entity.setPageNumber(pageNumber);
        entity.setCreatedAt(createdAt);
        return entity;
    }

    private Note buildNote(UUID id, UUID bookId, NoteType type) {
        return new Note(
            id,
            bookId,
            type,
            textContent,
            fileName,
            pageNumber,
            createdAt
        );
    }
}