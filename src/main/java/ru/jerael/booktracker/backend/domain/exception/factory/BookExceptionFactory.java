package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.code.BookErrorCode;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import java.util.Map;
import java.util.UUID;

public class BookExceptionFactory {
    public static NotFoundException notFound(UUID id) {
        return new NotFoundException(
            BookErrorCode.BOOK_NOT_FOUND,
            "Book with id " + id + " was not found"
        );
    }

    public static ValidationException invalidStatus(String status) {
        return new ValidationException(
            BookErrorCode.INVALID_STATUS,
            "Unknown book status: " + status,
            "status",
            Map.of("supported", BookStatus.values())
        );
    }
}
