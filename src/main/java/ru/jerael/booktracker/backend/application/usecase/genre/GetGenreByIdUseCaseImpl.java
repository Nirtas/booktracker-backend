package ru.jerael.booktracker.backend.application.usecase.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.exception.factory.GenreExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.genre.Genre;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenreByIdUseCase;

@UseCase
@RequiredArgsConstructor
public class GetGenreByIdUseCaseImpl implements GetGenreByIdUseCase {
    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public Genre execute(Integer id) {
        return genreRepository.findById(id).orElseThrow(() -> GenreExceptionFactory.genreNotFound(id));
    }
}
