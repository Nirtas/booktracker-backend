package ru.jerael.booktracker.backend.web.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.jerael.booktracker.backend.domain.constant.AuthorRules;
import ru.jerael.booktracker.backend.domain.constant.BookRules;
import ru.jerael.booktracker.backend.domain.constant.ValidationRegex;
import java.util.Set;

public record BookDetailsUpdateRequest(
    @Nullable
    @Schema(example = "Book Title")
    @Pattern(regexp = ValidationRegex.PATTERN_NOT_BLANK, message = "must not be blank")
    @Size(max = BookRules.TITLE_MAX_LENGTH)
    String title,

    @Nullable
    @Schema(example = "READING")
    String status,

    @Nullable
    Set<Integer> genreIds,

    @Nullable
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
