package ru.jerael.booktracker.backend.domain.usecase.book;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;

public interface CreateBookUseCase {
    Book execute(BookCreation data);
}
