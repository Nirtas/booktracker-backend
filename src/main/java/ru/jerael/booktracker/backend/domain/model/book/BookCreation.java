package ru.jerael.booktracker.backend.domain.model.book;

import java.util.Set;

public record BookCreation(
    String title,
    String author,
    BookStatus status,
    Set<Integer> genreIds
) {}
