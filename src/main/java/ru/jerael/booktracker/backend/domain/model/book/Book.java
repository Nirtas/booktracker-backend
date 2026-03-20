package ru.jerael.booktracker.backend.domain.model.book;

import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.model.note.Note;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record Book(
    UUID id,
    String title,
    String coverFileName,
    Instant createdAt,
    Set<Genre> genres,
    Set<Author> authors,
    String description,
    Publisher publisher,
    Language language,
    LocalDate publishedOn,
    Integer totalPages,
    String isbn10,
    String isbn13,
    List<ReadingAttempt> attempts,
    List<Note> notes
) {
    public BookStatus status() {
        if (attempts == null || attempts.isEmpty()) {
            return BookStatus.defaultStatus();
        }

        return attempts.stream()
            .max(Comparator.comparing(ReadingAttempt::startedAt))
            .map(ReadingAttempt::status)
            .orElse(BookStatus.defaultStatus());
    }
}
