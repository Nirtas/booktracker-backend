package ru.jerael.booktracker.backend.web.mapper;

import ru.jerael.booktracker.backend.domain.model.note.Note;
import ru.jerael.booktracker.backend.web.annotation.WebMapper;
import ru.jerael.booktracker.backend.web.dto.note.NoteResponse;

@WebMapper
public class NoteWebMapper {
    public NoteResponse toResponse(Note note) {
        if (note == null) return null;

        return new NoteResponse(
            note.id(),
            note.type().name(),
            note.textContent(),
            note.fileName(),
            note.pageNumber(),
            note.createdAt()
        );
    }
}
