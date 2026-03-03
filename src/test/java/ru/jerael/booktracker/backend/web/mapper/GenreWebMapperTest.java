package ru.jerael.booktracker.backend.web.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.web.dto.genre.GenreResponse;
import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GenreWebMapperTest {
    private final GenreWebMapper genreWebMapper = new GenreWebMapper();

    @Test
    void toResponse() {
        Genre genre = new Genre(1, "adventure");

        GenreResponse genreResponse = genreWebMapper.toResponse(genre);

        assertEquals(1, genreResponse.id());
        assertEquals("adventure", genreResponse.name());
    }

    @Test
    void toResponses() {
        Genre genre1 = new Genre(1, "adventure");
        Genre genre2 = new Genre(2, "action");
        Set<Genre> genres = Set.of(genre1, genre2);

        Set<GenreResponse> genreResponses = genreWebMapper.toResponses(genres);

        assertEquals(2, genreResponses.size());
        assertThat(genreResponses).extracting(GenreResponse::id).containsExactlyInAnyOrder(1, 2);
        assertThat(genreResponses).extracting(GenreResponse::name).containsExactlyInAnyOrder("adventure", "action");
    }
}