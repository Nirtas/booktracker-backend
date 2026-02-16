package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaGenreRepository;
import ru.jerael.booktracker.backend.data.mapper.GenreDataMapper;
import ru.jerael.booktracker.backend.domain.model.Genre;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({GenreRepositoryImpl.class, GenreDataMapper.class})
class GenreRepositoryImplTest {

    @Autowired
    private GenreRepositoryImpl genreRepository;

    @Autowired
    private JpaGenreRepository jpaGenreRepository;

    @Test
    void getGenres_ShouldReturnAllGenres() {
        GenreEntity entity1 = new GenreEntity();
        entity1.setName("adventure");
        GenreEntity entity2 = new GenreEntity();
        entity2.setName("action");
        jpaGenreRepository.saveAll(List.of(entity1, entity2));

        Set<Genre> result = genreRepository.getGenres();

        assertEquals(2, result.size());
        assertThat(result).extracting(Genre::name).containsExactlyInAnyOrder("adventure", "action");
    }

    @Test
    void getGenreById_WhenExists_ShouldReturnGenre() {
        GenreEntity entity = new GenreEntity();
        entity.setName("adventure");
        GenreEntity savedEntity = jpaGenreRepository.save(entity);

        Optional<Genre> result = genreRepository.getGenreById(savedEntity.getId());

        assertTrue(result.isPresent());
        assertEquals("adventure", result.get().name());
        assertEquals(savedEntity.getId(), result.get().id());
    }
}