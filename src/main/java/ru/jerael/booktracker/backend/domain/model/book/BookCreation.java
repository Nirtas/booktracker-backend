package ru.jerael.booktracker.backend.domain.model.book;

import jakarta.annotation.Nullable;
import java.util.Set;

public record BookCreation(
    String title,
    String author,
    @Nullable BookStatus status,
    Set<Integer> genreIds
) {}
