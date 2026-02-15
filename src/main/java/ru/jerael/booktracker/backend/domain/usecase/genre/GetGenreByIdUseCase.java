package ru.jerael.booktracker.backend.domain.usecase.genre;

import ru.jerael.booktracker.backend.domain.model.Genre;

public interface GetGenreByIdUseCase {
    Genre execute(Integer id);
}
