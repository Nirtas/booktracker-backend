package ru.jerael.booktracker.backend.domain.usecase.genre;

import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.Set;

public interface GetGenresUseCase {
    Set<Genre> execute();
}
