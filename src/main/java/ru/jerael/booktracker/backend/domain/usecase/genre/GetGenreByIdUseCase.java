package ru.jerael.booktracker.backend.domain.usecase.genre;

import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.Optional;

public interface GetGenreByIdUseCase {
    Optional<Genre> execute(Integer id);
}
