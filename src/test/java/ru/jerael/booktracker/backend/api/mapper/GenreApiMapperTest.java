package ru.jerael.booktracker.backend.api.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.api.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GenreApiMapperTest {
    private final GenreApiMapper genreApiMapper = new GenreApiMapper();

    @Test
    void toResponse() {
        Genre genre = new Genre(1, "adventure");

        GenreResponse genreResponse = genreApiMapper.toResponse(genre);

        assertEquals(1, genreResponse.id());
        assertEquals("adventure", genreResponse.name());
    }

    @Test
    void toResponses() {
        Genre genre1 = new Genre(1, "adventure");
        Genre genre2 = new Genre(2, "action");
        List<Genre> genres = List.of(genre1, genre2);

        List<GenreResponse> genreResponses = genreApiMapper.toResponses(genres);

        assertEquals(1, genreResponses.get(0).id());
        assertEquals("adventure", genreResponses.get(0).name());
        assertEquals(2, genreResponses.get(1).id());
        assertEquals("action", genreResponses.get(1).name());
    }
}