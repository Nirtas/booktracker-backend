package ru.jerael.booktracker.backend.web.dto.book;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import ru.jerael.booktracker.backend.domain.constant.BookRules;
import java.util.Set;

public record BookCreationRequest(
    @NotBlank
    @Size(max = BookRules.TITLE_MAX_LENGTH)
    String title,

    @NotBlank
    @Size(max = BookRules.AUTHOR_MAX_LENGTH)
    String author,

    @Nullable
    String status,

    @NotEmpty
    Set<Integer> genreIds
) {}
