package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository {
    PageResult<Book> findAllByUserId(PageQuery query, UUID userId);

    Optional<Book> findByIdAndUserId(UUID id, UUID userId);

    Book save(Book book, UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);
}
