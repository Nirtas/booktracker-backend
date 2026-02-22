package ru.jerael.booktracker.backend.domain.exception;

import java.util.Set;
import java.util.UUID;

public class NotFoundException extends AppException {
    public NotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }

    public static NotFoundException genreNotFound(Integer id) {
        return new NotFoundException("Genre not found with id: " + id, "GENRE_NOT_FOUND");
    }

    public static NotFoundException genresNotFound(Set<Integer> ids) {
        return new NotFoundException("Genres not found with ids: " + ids, "GENRE_NOT_FOUND");
    }

    public static NotFoundException bookNotFound(UUID id) {
        return new NotFoundException("Book with id " + id + " was not found", "BOOK_NOT_FOUND");
    }
}
