package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.genre.Genre;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository {
    Set<Genre> findAll();

    Optional<Genre> findById(Integer id);

    Set<Genre> findAllById(Set<Integer> ids);
}
