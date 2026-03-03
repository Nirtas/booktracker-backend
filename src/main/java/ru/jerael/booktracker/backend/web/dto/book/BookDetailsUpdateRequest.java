package ru.jerael.booktracker.backend.web.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Schema(example = "Book Author")
    @Pattern(regexp = ValidationRegex.PATTERN_NOT_BLANK, message = "must not be blank")
    @Size(max = BookRules.AUTHOR_MAX_LENGTH)
    String author,

    @Nullable
    @Schema(example = "READING")
    String status,

    @Nullable
    Set<Integer> genreIds
) {}
