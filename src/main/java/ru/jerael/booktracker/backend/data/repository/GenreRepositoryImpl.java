package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaGenreRepository;
import ru.jerael.booktracker.backend.data.mapper.GenreMapper;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryImpl implements GenreRepository {
    private final JpaGenreRepository jpaGenreRepository;
    private final GenreMapper genreMapper;

    @Override
    public List<Genre> getGenres() {
        List<GenreEntity> entities = jpaGenreRepository.findAll();
        return entities.stream().map(genreMapper::toDomain).toList();
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        return jpaGenreRepository.findById(id).map(genreMapper::toDomain);
    }
}
