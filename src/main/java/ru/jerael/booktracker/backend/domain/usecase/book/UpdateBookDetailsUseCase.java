package ru.jerael.booktracker.backend.domain.usecase.book;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import java.util.UUID;

public interface UpdateBookDetailsUseCase {
    Book execute(UUID id, UUID userId, BookDetailsUpdate data);
}
