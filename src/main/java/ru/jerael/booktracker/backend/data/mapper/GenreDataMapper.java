package ru.jerael.booktracker.backend.data.mapper;

import ru.jerael.booktracker.backend.data.annotation.DataMapper;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import ru.jerael.booktracker.backend.domain.model.Genre;

@DataMapper
public class GenreDataMapper {
    public Genre toDomain(GenreEntity entity) {
        if (entity == null) return null;

        return new Genre(entity.getId(), entity.getName());
    }

    public GenreEntity toEntity(Genre genre) {
        if (genre == null) return null;

        GenreEntity entity = new GenreEntity();
        entity.setId(genre.id());
        entity.setName(genre.name());
        return entity;
    }
}
