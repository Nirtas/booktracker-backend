package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;
import ru.jerael.booktracker.backend.data.db.entity.AuthorEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaAuthorRepository;
import ru.jerael.booktracker.backend.data.mapper.AuthorDataMapper;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({AuthorRepositoryImpl.class, AuthorDataMapper.class})
class AuthorRepositoryImplTest {

    @Autowired
    private AuthorRepositoryImpl authorRepository;

    @Autowired
    private JpaAuthorRepository jpaAuthorRepository;

    private final String fullName = "Full Name";

    @Test
    void findByFullName_WhenExists_ShouldReturnAuthor() {
        AuthorEntity entity = new AuthorEntity();
        entity.setFullName(fullName);
        AuthorEntity savedEntity = jpaAuthorRepository.save(entity);

        Optional<Author> result = authorRepository.findByFullName("full name");

        assertTrue(result.isPresent());
        assertEquals(savedEntity.getId(), result.get().id());
        assertEquals(fullName, result.get().fullName());
    }

    @Test
    void save_WhenIdIsNull_ShouldInsertNewAuthor() {
        Author author = new Author(null, fullName);

        Author result = authorRepository.save(author);

        assertNotNull(result.id());
        assertEquals(fullName, result.fullName());
    }

    @Test
    void save_WhenIdIsPresent_ShouldUpdateExistingAuthor() {
        Author author = new Author(null, fullName);

        UUID savedId = authorRepository.save(author).id();

        Author newAuthor = new Author(savedId, "New Name");

        Author result = authorRepository.save(newAuthor);

        assertEquals(savedId, result.id());
        assertEquals("New Name", result.fullName());
    }
}