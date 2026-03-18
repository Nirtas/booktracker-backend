package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.AuthorEntity;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorDataMapperTest {
    private final AuthorDataMapper authorDataMapper = new AuthorDataMapper();

    private final UUID id = UUID.fromString("afe42b20-8148-457e-9c97-8c4db85a71a9");
    private final String fullName = "Full Name";

    @Test
    void toEntity() {
        Author author = new Author(id, fullName);

        AuthorEntity entity = authorDataMapper.toEntity(author);

        assertEquals(id, entity.getId());
        assertEquals(fullName, entity.getFullName());
    }

    @Test
    void toDomain() {
        AuthorEntity entity = new AuthorEntity();
        entity.setId(id);
        entity.setFullName(fullName);

        Author author = authorDataMapper.toDomain(entity);

        assertEquals(id, author.id());
        assertEquals(fullName, author.fullName());
    }
}