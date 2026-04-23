package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.AuthorEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaAuthorRepository;
import ru.jerael.booktracker.backend.data.mapper.AuthorDataMapper;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.repository.AuthorRepository;
import java.util.Optional;

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
}
