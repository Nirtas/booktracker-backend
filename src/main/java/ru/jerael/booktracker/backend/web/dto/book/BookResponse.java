package ru.jerael.booktracker.backend.web.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import ru.jerael.booktracker.backend.web.dto.author.AuthorResponse;
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.web.dto.language.LanguageResponse;
import ru.jerael.booktracker.backend.web.dto.note.NoteResponse;
import ru.jerael.booktracker.backend.web.dto.publisher.PublisherResponse;
import ru.jerael.booktracker.backend.web.dto.reading_attempt.ReadingAttemptResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record BookResponse(
    UUID id,
    String title,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @Nullable
    String coverUrl,

    String status,
    Instant createdAt,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    Set<GenreResponse> genres,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    Set<AuthorResponse> authors,

    String description,
    PublisherResponse publisher,
    LanguageResponse language,
    LocalDate publishedOn,
    Integer totalPages,
    String isbn10,
    String isbn13,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    List<ReadingAttemptResponse> attempts,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    List<NoteResponse> notes
) {}
