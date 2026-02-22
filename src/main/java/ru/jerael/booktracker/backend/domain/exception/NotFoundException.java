package ru.jerael.booktracker.backend.domain.exception;

import java.util.Set;
import java.util.UUID;

public class NotFoundException extends AppException {
    public NotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }

    public static NotFoundException genreNotFound(Integer id) {
        return new NotFoundException(
            "GENRE_NOT_FOUND",
            "Genre not found with id: " + id
        );
    }

    public static NotFoundException genresNotFound(Set<Integer> ids) {
        return new NotFoundException(
            "GENRE_NOT_FOUND",
            "Genres not found with ids: " + ids
        );
    }

    public static NotFoundException bookNotFound(UUID id) {
        return new NotFoundException(
            "BOOK_NOT_FOUND",
            "Book with id " + id + " was not found"
        );
    }
}
