package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.AuthorEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaAuthorRepository;
import ru.jerael.booktracker.backend.data.mapper.AuthorDataMapper;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.repository.AuthorRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AuthorRepositoryImpl implements AuthorRepository {
    private final JpaAuthorRepository jpaAuthorRepository;
    private final AuthorDataMapper authorDataMapper;

    @Override
    public Optional<Author> findByFullName(String fullName) {
        return jpaAuthorRepository.findByFullNameIgnoreCase(fullName).map(authorDataMapper::toDomain);
    }

    @Override
    public Author save(Author author) {
        AuthorEntity entity = authorDataMapper.toEntity(author);
        AuthorEntity savedEntity = jpaAuthorRepository.save(entity);
        return authorDataMapper.toDomain(savedEntity);
    }

    @Override
    public Set<Author> findAllByNames(Set<String> names) {
        return jpaAuthorRepository.findAllByFullNameInIgnoreCase(names).stream()
            .map(authorDataMapper::toDomain)
            .collect(Collectors.toSet());
    }

    @Override
    public PageResult<Author> searchByFullName(PageQuery pageQuery, String query) {
        int page = pageQuery != null ? pageQuery.page() : 0;
        int size = pageQuery != null ? pageQuery.size() : 5;
        Pageable pageable = PageRequest.of(page, size);

        List<AuthorEntity> entities = jpaAuthorRepository.searchBySimilarity(query, pageable);
        return new PageResult<>(
            entities.stream().map(authorDataMapper::toDomain).toList(),
            size,
            page,
            entities.size(),
            1
        );
    }
}
