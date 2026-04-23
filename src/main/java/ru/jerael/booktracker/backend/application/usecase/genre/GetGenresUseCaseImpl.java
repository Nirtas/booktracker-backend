package ru.jerael.booktracker.backend.application.usecase.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.model.genre.Genre;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenresUseCase;
import java.util.Set;

@UseCase
@RequiredArgsConstructor
public class GetGenresUseCaseImpl implements GetGenresUseCase {
    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public Set<Genre> execute() {
        return genreRepository.findAll();
    }
}
