package ru.jerael.booktracker.backend.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import ru.jerael.booktracker.backend.web.config.WebProperties;
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.web.exception.handler.GlobalExceptionHandler;
import ru.jerael.booktracker.backend.web.mapper.GenreWebMapper;
import ru.jerael.booktracker.backend.domain.exception.factory.GenreExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenreByIdUseCase;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenresUseCase;
import java.util.Set;
import static org.mockito.Mockito.when;

@WebMvcTest(GenreController.class)
@Import({GlobalExceptionHandler.class, GenreWebMapper.class})
@AutoConfigureRestTestClient
class GenreControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private WebProperties webProperties;

    @MockitoBean
    private GetGenresUseCase getGenresUseCase;

    @MockitoBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @Test
    void getAll_ShouldReturnSetOfGenres() {
        Genre genre = new Genre(1, "adventure");
        GenreResponse genreResponse = new GenreResponse(1, "adventure");
        when(getGenresUseCase.execute()).thenReturn(Set.of(genre));

        restTestClient.get().uri("/api/v1/genres")
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<Set<GenreResponse>>() {})
            .isEqualTo(Set.of(genreResponse));
    }

    @Test
    void getById_WhenExceptionThrown_ShouldReturnNotFound() {
        Integer genreId = 5555;
        when(getGenreByIdUseCase.execute(genreId)).thenThrow(GenreExceptionFactory.notFound(genreId));

        restTestClient.get().uri("/api/v1/genres/" + genreId)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.detail").isEqualTo("Genre not found with id: " + genreId);
    }
}