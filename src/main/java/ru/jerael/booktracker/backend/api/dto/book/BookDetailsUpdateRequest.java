package ru.jerael.booktracker.backend.api.dto.book;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.jerael.booktracker.backend.domain.constants.BookRules;
import java.util.Set;

public record BookDetailsUpdateRequest(
    @Nullable
    @Pattern(regexp = ".*\\S+.*", message = "must not be blank")
    @Size(max = BookRules.TITLE_MAX_LENGTH)
    String title,

    @Nullable
    @Pattern(regexp = ".*\\S+.*", message = "must not be blank")
    @Size(max = BookRules.AUTHOR_MAX_LENGTH)
    String author,

    @Nullable
    String status,

    @Nullable
    Set<Integer> genreIds
) {}
