package ru.jerael.booktracker.backend.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import ru.jerael.booktracker.backend.api.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.api.handler.GlobalExceptionHandler;
import ru.jerael.booktracker.backend.api.mapper.GenreApiMapper;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenreByIdUseCase;
import ru.jerael.booktracker.backend.domain.usecase.genre.GetGenresUseCase;
import java.util.List;
import static org.mockito.Mockito.when;

@WebMvcTest(GenreController.class)
@Import({GlobalExceptionHandler.class, GenreApiMapper.class})
@AutoConfigureRestTestClient
class GenreControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private GetGenresUseCase getGenresUseCase;

    @MockitoBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @Test
    void getAll_ShouldReturnListOfGenres() {
        Genre genre = new Genre(1, "adventure");
        GenreResponse genreResponse = new GenreResponse(1, "adventure");
        when(getGenresUseCase.execute()).thenReturn(List.of(genre));

        restTestClient.get().uri("/api/v1/genres")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<GenreResponse>>() {})
                .isEqualTo(List.of(genreResponse));
    }

    @Test
    void getById_WhenExceptionThrown_ShouldReturnNotFound() {
        Integer genreId = 5555;
        when(getGenreByIdUseCase.execute(genreId)).thenThrow(NotFoundException.genreNotFound(genreId));

        restTestClient.get().uri("/api/v1/genres/" + genreId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Genre with id " + genreId + " was not found");
    }
}