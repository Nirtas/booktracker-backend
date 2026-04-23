package ru.jerael.booktracker.backend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jerael.booktracker.backend.domain.model.genre.Genre;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenreByIdUseCase;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenresUseCase;
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.web.mapper.GenreWebMapper;
import java.util.Set;

@Tag(name = "Genres")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/genres")
public class GenreController {
    private final GetGenresUseCase getGenresUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final GenreWebMapper genreWebMapper;

    @Operation(summary = "Get all genres")
    @GetMapping
    public Set<GenreResponse> getAll() {
        Set<Genre> genres = getGenresUseCase.execute();
        return genreWebMapper.toResponses(genres);
    }

    @Operation(summary = "Get genre by id")
    @GetMapping("/{id}")
    public GenreResponse getById(@PathVariable Integer id) {
        Genre genre = getGenreByIdUseCase.execute(id);
        return genreWebMapper.toResponse(genre);
    }
}
