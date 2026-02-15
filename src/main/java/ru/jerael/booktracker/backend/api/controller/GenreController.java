package ru.jerael.booktracker.backend.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jerael.booktracker.backend.api.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.api.mapper.GenreApiMapper;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenreByIdUseCase;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenresUseCase;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/genres")
public class GenreController {
    private final GetGenresUseCase getGenresUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final GenreApiMapper genreApiMapper;

    @GetMapping
    public List<GenreResponse> getAll() {
        List<Genre> genres = getGenresUseCase.execute();
        return genreApiMapper.toResponses(genres);
    }

    @GetMapping("/{id}")
    public GenreResponse getById(@PathVariable Integer id) {
        Genre genre = getGenreByIdUseCase.execute(id);
        return genreApiMapper.toResponse(genre);
    }
}
