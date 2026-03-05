package ru.jerael.booktracker.backend.domain.usecase.book;

import ru.jerael.booktracker.backend.domain.model.book.Book;
import java.util.UUID;

public interface GetBookByIdUseCase {
    Book execute(UUID id, UUID userId);
}
