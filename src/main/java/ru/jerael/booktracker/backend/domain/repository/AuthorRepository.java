package ru.jerael.booktracker.backend.domain.repository;

import ru.jerael.booktracker.backend.domain.model.author.Author;
import java.util.Optional;
import java.util.Set;

public interface AuthorRepository {
    Optional<Author> findByFullName(String fullName);

    Author save(Author author);

    Set<Author> findAllByNames(Set<String> names);
}
