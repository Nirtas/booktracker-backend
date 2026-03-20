package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.NoteEntity;
import ru.jerael.booktracker.backend.domain.model.note.Note;
import ru.jerael.booktracker.backend.domain.model.note.NoteType;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NoteDataMapperTest {
    private final NoteDataMapper noteDataMapper = new NoteDataMapper();

    private final UUID id = UUID.fromString("a774501c-002b-4420-8788-6f9828b4b419");
    private final UUID bookId = UUID.fromString("cb1285f1-9084-4e3b-a7ed-9cb62699f930");
    private final UUID userId = UUID.fromString("ee21a506-2e0c-4689-a341-ee66654387a3");
    private final NoteType type = NoteType.TEXT;
    private final String textContent = "text content";
    private final String fileName = "file_name.txt";
    private final int pageNumber = 123;
    private final Instant createdAt = Instant.ofEpochMilli(1773826150059L);

    @Test
    void toEntity() {
        Note note = new Note(id, bookId, type, textContent, fileName, pageNumber, createdAt);

        NoteEntity result = noteDataMapper.toEntity(note);

        assertEquals(id, result.getId());
        assertEquals(bookId, result.getBook().getId());
        assertEquals(type, result.getType());
        assertEquals(textContent, result.getTextContent());
        assertEquals(fileName, result.getFileName());
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(createdAt, result.getCreatedAt());
    }

    @Test
    void toDomain() {
        BookEntity book = new BookEntity();
        book.setId(bookId);
        book.setUserId(userId);
        book.setTitle("title");
        book.setCoverFileName(null);
        book.setCreatedAt(Instant.now());
        book.setGenres(Collections.emptySet());

        NoteEntity entity = new NoteEntity();
        entity.setId(id);
        entity.setBook(book);
        entity.setType(type);
        entity.setTextContent(textContent);
        entity.setFileName(fileName);
        entity.setPageNumber(pageNumber);
        entity.setCreatedAt(createdAt);

        Note result = noteDataMapper.toDomain(entity);

        assertEquals(id, result.id());
        assertEquals(bookId, result.bookId());
        assertEquals(type, result.type());
        assertEquals(textContent, result.textContent());
        assertEquals(fileName, result.fileName());
        assertEquals(pageNumber, result.pageNumber());
        assertEquals(createdAt, result.createdAt());
    }
}