package ru.jerael.booktracker.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.jerael.booktracker.backend.application.usecase.genre.GetGenreByIdUseCaseImpl;
import ru.jerael.booktracker.backend.application.usecase.genre.GetGenresUseCaseImpl;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenreByIdUseCase;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenresUseCase;

@Configuration
public class GenreConfig {

    @Bean
    public GetGenresUseCase getGenresUseCase(GenreRepository genreRepository) {
        return new GetGenresUseCaseImpl(genreRepository);
    }

    @Bean
    public GetGenreByIdUseCase getGenreByIdUseCase(GenreRepository genreRepository) {
        return new GetGenreByIdUseCaseImpl(genreRepository);
    }
}
