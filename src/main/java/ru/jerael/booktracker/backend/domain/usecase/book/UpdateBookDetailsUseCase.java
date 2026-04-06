package ru.jerael.booktracker.backend.domain.usecase.book;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;

public interface UpdateBookDetailsUseCase {
    Book execute(BookDetailsUpdate data);
}
