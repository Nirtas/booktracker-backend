package ru.jerael.booktracker.backend.domain.model.book;

import jakarta.annotation.Nullable;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.model.note.Note;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.domain.model.reading_attempt.ReadingAttempt;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record Book(
    UUID id,
    String title,
    String author,
    @Nullable String coverFileName,
    BookStatus status,
    Instant createdAt,
    Set<Genre> genres,
    Set<Author> authors,
    @Nullable String description,
    @Nullable Publisher publisher,
    @Nullable Language language,
    @Nullable LocalDate publishedOn,
    @Nullable Integer totalPages,
    @Nullable String isbn10,
    @Nullable String isbn13,
    List<ReadingAttempt> attempts,
    List<Note> notes
) {}
