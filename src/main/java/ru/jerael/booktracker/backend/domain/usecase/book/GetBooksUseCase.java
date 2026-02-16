package ru.jerael.booktracker.backend.domain.usecase.book;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import java.util.List;

public interface GetBooksUseCase {
    List<Book> execute();
}
