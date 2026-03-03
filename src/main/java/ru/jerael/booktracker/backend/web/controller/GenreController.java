package ru.jerael.booktracker.backend.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.web.mapper.GenreWebMapper;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenreByIdUseCase;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenresUseCase;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/genres")
public class GenreController {
    private final GetGenresUseCase getGenresUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final GenreWebMapper genreWebMapper;

    @GetMapping
    public Set<GenreResponse> getAll() {
        Set<Genre> genres = getGenresUseCase.execute();
        return genreWebMapper.toResponses(genres);
    }

    @GetMapping("/{id}")
    public GenreResponse getById(@PathVariable Integer id) {
        Genre genre = getGenreByIdUseCase.execute(id);
        return genreWebMapper.toResponse(genre);
    }
}
