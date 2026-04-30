package ru.jerael.booktracker.backend.web.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import ru.jerael.booktracker.backend.web.dto.author.AuthorResponse;
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.web.dto.language.LanguageResponse;
import ru.jerael.booktracker.backend.web.dto.publisher.PublisherResponse;
import java.util.Set;

public record BookMetadataResponse(

    String title,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @Nullable
    String coverUrl,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    Set<GenreResponse> genres,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    Set<AuthorResponse> authors,

    String description,
    PublisherResponse publisher,
    LanguageResponse language,
    Integer publishedOn,
    Integer totalPages,
    String isbn10,
    String isbn13
) {}
