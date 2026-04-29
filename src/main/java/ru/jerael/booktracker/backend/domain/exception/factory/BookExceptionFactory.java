package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.UnprocessableContentException;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.code.BookErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import java.util.Map;
import java.util.UUID;

public class BookExceptionFactory {
    public static NotFoundException bookNotFound(UUID id) {
        return new NotFoundException(
            BookErrorCode.BOOK_NOT_FOUND,
            "Book with id " + id + " was not found"
        );
    }

    public static ValidationException invalidStatus(String status) {
        return new ValidationException(
            new ValidationError(
                BookErrorCode.INVALID_BOOK_STATUS.name(),
                "status",
                "Unknown book status: " + status,
                Map.of("supported", BookStatus.values())
            )
        );
    }

    public static NotFoundException coverNotFound(UUID id) {
        return new NotFoundException(
            BookErrorCode.COVER_NOT_FOUND,
            "Cover for book with id " + id + " was not found"
        );
    }

    public static NotFoundException readingAttemptsNotFound(UUID id) {
        return new NotFoundException(
            BookErrorCode.READING_ATTEMPTS_NOT_FOUND,
            "Book with id " + id + " has no reading attempts"
        );
    }

    public static UnprocessableContentException invalidStatusTransition(BookStatus from, BookStatus to) {
        return new UnprocessableContentException(
            BookErrorCode.INVALID_BOOK_STATUS_TRANSITION,
            "Transition from " + from + " to " + to + " is not allowed",
            "status"
        );
    }

    public static NotFoundException bookMetadataNotFound() {
        return new NotFoundException(
            BookErrorCode.BOOK_METADATA_NOT_FOUND,
            "Book metadata not found"
        );
    }
}
