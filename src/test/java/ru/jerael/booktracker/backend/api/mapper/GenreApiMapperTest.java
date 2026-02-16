package ru.jerael.booktracker.backend.api.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.api.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
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
        Set<Genre> genres = Set.of(genre1, genre2);

        Set<GenreResponse> genreResponses = genreApiMapper.toResponses(genres);

        assertEquals(2, genreResponses.size());
        assertThat(genreResponses).extracting(GenreResponse::id).containsExactlyInAnyOrder(1, 2);
        assertThat(genreResponses).extracting(GenreResponse::name).containsExactlyInAnyOrder("adventure", "action");
    }
}