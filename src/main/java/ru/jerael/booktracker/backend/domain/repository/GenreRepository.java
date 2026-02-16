package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository {
    Set<Genre> getGenres();

    Optional<Genre> getGenreById(Integer id);
}
