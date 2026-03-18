package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.note.Note;
import java.util.List;
import java.util.UUID;

public interface NoteRepository {
    List<Note> findAllByBookId(UUID bookId);

    Note save(Note note);
}
