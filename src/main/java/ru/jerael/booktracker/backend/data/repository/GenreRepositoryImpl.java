package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
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

    @Override
    public Set<Genre> getGenres() {
        List<GenreEntity> entities = jpaGenreRepository.findAllByOrderByIdAsc();
        return entities.stream().map(genreDataMapper::toDomain).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        return jpaGenreRepository.findById(id).map(genreDataMapper::toDomain);
    }

    @Override
    public Set<Genre> findAllById(Set<Integer> ids) {
        return jpaGenreRepository.findAllById(ids).stream().map(genreDataMapper::toDomain).collect(Collectors.toSet());
    }
}
