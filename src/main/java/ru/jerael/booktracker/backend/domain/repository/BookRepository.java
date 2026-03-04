package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository {
    PageResult<Book> findAll(PageQuery query);

    Optional<Book> findById(UUID id);

    Book save(Book book);

    void deleteById(UUID id);
}
