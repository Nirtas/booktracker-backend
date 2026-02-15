package ru.jerael.booktracker.backend.domain.usecase.genre;

import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.List;

public interface GetGenresUseCase {
    List<Genre> execute();
}
