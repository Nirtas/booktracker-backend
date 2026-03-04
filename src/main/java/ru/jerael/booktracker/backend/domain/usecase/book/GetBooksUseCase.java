package ru.jerael.booktracker.backend.domain.usecase.book;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;

public interface GetBooksUseCase {
    PageResult<Book> execute(PageQuery query);
}
