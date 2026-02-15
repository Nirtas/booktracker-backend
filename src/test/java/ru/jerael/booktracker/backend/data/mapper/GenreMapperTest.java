package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import ru.jerael.booktracker.backend.domain.model.Genre;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GenreMapperTest {
    private final GenreMapper genreMapper = new GenreMapper();

    @Test
    void toDomain() {
        GenreEntity entity = new GenreEntity();
        entity.setId(1);
        entity.setName("adventure");

        Genre genre = genreMapper.toDomain(entity);

        assertEquals(1, genre.id());
        assertEquals("adventure", genre.name());
    }

    @Test
    void toEntity() {
        Genre genre = new Genre(1, "adventure");

        GenreEntity entity = genreMapper.toEntity(genre);

        assertEquals(1, entity.getId());
        assertEquals("adventure", entity.getName());
    }
}