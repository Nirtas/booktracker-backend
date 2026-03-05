package ru.jerael.booktracker.backend.domain.usecase.book;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import java.util.UUID;

public interface CreateBookUseCase {
    Book execute(BookCreation data, UUID userId);
}
