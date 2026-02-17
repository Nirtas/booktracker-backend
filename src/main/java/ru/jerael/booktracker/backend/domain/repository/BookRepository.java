package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BookRepository {
    List<Book> getBooks();

    Optional<Book> getBookById(UUID id);

    Book create(BookCreation data, Set<Genre> genres);

    Book updateCoverUrl(UUID bookId, String url);
}
