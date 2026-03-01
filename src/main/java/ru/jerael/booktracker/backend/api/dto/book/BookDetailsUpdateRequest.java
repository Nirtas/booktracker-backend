package ru.jerael.booktracker.backend.api.dto.book;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.jerael.booktracker.backend.domain.constant.BookRules;
import ru.jerael.booktracker.backend.domain.constant.ValidationRegex;
import java.util.Set;

public record BookDetailsUpdateRequest(
    @Nullable
    @Pattern(regexp = ValidationRegex.PATTERN_NOT_BLANK, message = "must not be blank")
    @Size(max = BookRules.TITLE_MAX_LENGTH)
    String title,

    @Nullable
    @Pattern(regexp = ValidationRegex.PATTERN_NOT_BLANK, message = "must not be blank")
    @Size(max = BookRules.AUTHOR_MAX_LENGTH)
    String author,

    @Nullable
    String status,

    @Nullable
    Set<Integer> genreIds
) {}
