package ru.jerael.booktracker.backend.application.usecase.genre;

import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenreByIdUseCase;

public class GetGenreByIdUseCaseImpl implements GetGenreByIdUseCase {
    private final GenreRepository genreRepository;

    public GetGenreByIdUseCaseImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre execute(Integer id) {
        return genreRepository.getGenreById(id).orElseThrow(() -> NotFoundException.genreNotFound(id));
    }
}
