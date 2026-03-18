package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.NoteEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaNoteRepository;
import ru.jerael.booktracker.backend.data.mapper.NoteDataMapper;
import ru.jerael.booktracker.backend.domain.model.note.Note;
import ru.jerael.booktracker.backend.domain.repository.NoteRepository;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NoteRepositoryImpl implements NoteRepository {
    private final JpaNoteRepository jpaNoteRepository;
    private final NoteDataMapper noteDataMapper;

    @Override
    public List<Note> findAllByBookId(UUID bookId) {
        return jpaNoteRepository.findAllByBookId(bookId).stream().map(noteDataMapper::toDomain).toList();
    }

    @Override
    public Note save(Note note) {
        NoteEntity entity = noteDataMapper.toEntity(note);
        NoteEntity savedEntity = jpaNoteRepository.save(entity);
        return noteDataMapper.toDomain(savedEntity);
    }
}
