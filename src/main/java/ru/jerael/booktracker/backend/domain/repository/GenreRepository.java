package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.List;
import java.util.Optional;

public interface GenreRepository {
    List<Genre> getGenres();

    Optional<Genre> getGenreById(Integer id);
}
