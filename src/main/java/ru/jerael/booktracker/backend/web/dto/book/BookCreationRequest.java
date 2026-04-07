package ru.jerael.booktracker.backend.web.dto.book;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import ru.jerael.booktracker.backend.domain.constant.AuthorRules;
import ru.jerael.booktracker.backend.domain.constant.BookRules;
import java.util.Set;

public record BookCreationRequest(
    @NotBlank
    @Size(max = BookRules.TITLE_MAX_LENGTH)
    String title,

    @Nullable
    String status,

    @NotEmpty
    Set<Integer> genreIds,

    @NotEmpty
    Set<@Size(max = AuthorRules.AUTHOR_FULL_NAME_MAX_LENGTH) String> authorNames,

    @Nullable
    String description,

    @Nullable
    String publisherName,

    @Nullable
    String languageCode,

    @Nullable
    @Min(BookRules.PUBLISHED_ON_MIN)
    @Max(BookRules.PUBLISHED_ON_MAX)
    Integer publishedOn,

    @Nullable
    Integer totalPages,

    @Nullable
    String isbn10,

    @Nullable
    String isbn13
) {}
