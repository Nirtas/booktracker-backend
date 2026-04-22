package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaGenreRepository;
import ru.jerael.booktracker.backend.data.mapper.GenreDataMapper;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryImpl implements GenreRepository {
    private final JpaGenreRepository jpaGenreRepository;
    private final GenreDataMapper genreDataMapper;

    @Cacheable(value = "genres", key = "'all'")
    @Override
    public Set<Genre> findAll() {
        List<GenreEntity> entities = jpaGenreRepository.findAllByOrderByNameAsc();
        return entities.stream().map(genreDataMapper::toDomain).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Cacheable(value = "genre", key = "#id", unless = "#result == null")
    @Override
    public Optional<Genre> findById(Integer id) {
        return jpaGenreRepository.findById(id).map(genreDataMapper::toDomain);
    }

    @Override
    public Set<Genre> findAllById(Set<Integer> ids) {
        return jpaGenreRepository.findAllById(ids).stream().map(genreDataMapper::toDomain).collect(Collectors.toSet());
    }
}
