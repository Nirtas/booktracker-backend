package ru.jerael.booktracker.backend.data.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.AuthorEntity;
import ru.jerael.booktracker.backend.domain.model.author.Author;

@Component
public class AuthorDataMapper {
    public AuthorEntity toEntity(Author author) {
        if (author == null) return null;

        AuthorEntity entity = new AuthorEntity();
        entity.setId(author.id());
        entity.setFullName(author.fullName());
        return entity;
    }

    public Author toDomain(AuthorEntity entity) {
        if (entity == null) return null;

        return new Author(
            entity.getId(),
            entity.getFullName()
        );
    }
}
