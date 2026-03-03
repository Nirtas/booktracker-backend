package ru.jerael.booktracker.backend.domain.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.code.GenreErrorCode;
import java.util.Set;

public class GenreExceptionFactory {
    public static NotFoundException genreNotFound(Integer id) {
        return new NotFoundException(
            GenreErrorCode.GENRE_NOT_FOUND,
            "Genre not found with id: " + id
        );
    }

    public static NotFoundException genresNotFound(Set<Integer> ids) {
        return new NotFoundException(
            GenreErrorCode.GENRE_NOT_FOUND,
            "Genres not found with ids: " + ids
        );
    }
}
