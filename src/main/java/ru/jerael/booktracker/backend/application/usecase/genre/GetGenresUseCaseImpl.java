package ru.jerael.booktracker.backend.application.usecase.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenresUseCase;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetGenresUseCaseImpl implements GetGenresUseCase {
    private final GenreRepository genreRepository;

    @Override
    public Set<Genre> execute() {
        return genreRepository.getGenres();
    }
}
