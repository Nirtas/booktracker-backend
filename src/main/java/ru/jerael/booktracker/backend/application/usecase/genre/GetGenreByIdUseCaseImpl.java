package ru.jerael.booktracker.backend.application.usecase.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.factory.GenreExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenreByIdUseCase;

@Service
@RequiredArgsConstructor
public class GetGenreByIdUseCaseImpl implements GetGenreByIdUseCase {
    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public Genre execute(Integer id) {
        return genreRepository.findById(id).orElseThrow(() -> GenreExceptionFactory.genreNotFound(id));
    }
}
