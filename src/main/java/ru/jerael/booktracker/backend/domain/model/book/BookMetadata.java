package ru.jerael.booktracker.backend.domain.model.book;

import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.genre.Genre;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import java.util.Set;

public record BookMetadata(
    String title,
    String cover,
    Set<Genre> genres,
    Set<Author> authors,
    String description,
    Publisher publisher,
    Language language,
    Integer publishedOn,
    Integer totalPages,
    String isbn10,
    String isbn13
) {}
