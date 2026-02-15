package ru.jerael.booktracker.backend.application.usecase.genre;

import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenresUseCase;
import java.util.List;

public class GetGenresUseCaseImpl implements GetGenresUseCase {
    private final GenreRepository genreRepository;

    public GetGenresUseCaseImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Genre> execute() {
        return genreRepository.getGenres();
    }
}
