package ru.jerael.booktracker.backend.domain.model.book;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BookDetailsUpdate(
    UUID bookId,
    UUID userId,
    String title,
    BookStatus status,
    Set<Integer> genreIds,
    Set<String> authorNames,
    String description,
    String publisherName,
    String languageCode,
    LocalDate publishedOn,
    Integer totalPages,
    String isbn10,
    String isbn13
) {}
