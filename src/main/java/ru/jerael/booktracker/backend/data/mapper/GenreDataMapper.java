package ru.jerael.booktracker.backend.data.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import ru.jerael.booktracker.backend.domain.model.Genre;

@Component
public class GenreDataMapper {
    public Genre toDomain(GenreEntity entity) {
        return new Genre(entity.getId(), entity.getName());
    }

    public GenreEntity toEntity(Genre genre) {
        GenreEntity entity = new GenreEntity();
        entity.setId(genre.id());
        entity.setName(genre.name());
        return entity;
    }
}
