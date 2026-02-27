package ru.jerael.booktracker.backend.domain.model.book;

import jakarta.annotation.Nullable;
import java.util.Set;

public record BookDetailsUpdate(
    @Nullable String title,
    @Nullable String author,
    @Nullable BookStatus status,
    @Nullable Set<Integer> genreIds
) {}
