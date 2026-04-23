package ru.jerael.booktracker.backend.data.mapper;

import ru.jerael.booktracker.backend.data.annotation.DataMapper;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.NoteEntity;
import ru.jerael.booktracker.backend.domain.model.note.Note;

@DataMapper
public class NoteDataMapper {
    public NoteEntity toEntity(Note note) {
        if (note == null) return null;

        NoteEntity entity = new NoteEntity();
        entity.setId(note.id());
        BookEntity book = new BookEntity();
        book.setId(note.bookId());
        entity.setBook(book);
        entity.setType(note.type());
        entity.setTextContent(note.textContent());
        entity.setFileName(note.fileName());
        entity.setPageNumber(note.pageNumber());
        entity.setCreatedAt(note.createdAt());
        return entity;
    }

    public Note toDomain(NoteEntity entity) {
        if (entity == null) return null;

        return new Note(
            entity.getId(),
            entity.getBook().getId(),
            entity.getType(),
            entity.getTextContent(),
            entity.getFileName(),
            entity.getPageNumber(),
            entity.getCreatedAt()
        );
    }
}
