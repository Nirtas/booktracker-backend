package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository;
import ru.jerael.booktracker.backend.data.db.repository.JpaGenreRepository;
import ru.jerael.booktracker.backend.data.mapper.BookDataMapper;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final JpaBookRepository jpaBookRepository;
    private final JpaGenreRepository jpaGenreRepository;
    private final BookDataMapper bookDataMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Book> getBooks() {
        List<BookEntity> entities = jpaBookRepository.findAll();
        return entities.stream().map(bookDataMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> getBookById(UUID id) {
        return jpaBookRepository.findById(id).map(bookDataMapper::toDomain);
    }

    @Override
    @Transactional
    public Book create(BookCreation data, Set<Genre> genres) {
        Set<GenreEntity> genreEntities = genres.stream()
            .map(genre -> jpaGenreRepository.getReferenceById(genre.id()))
            .collect(Collectors.toSet());
        BookEntity entity = bookDataMapper.toEntity(data, genreEntities);
        BookEntity savedBook = jpaBookRepository.saveAndFlush(entity);
        return bookDataMapper.toDomain(savedBook);
    }

    @Override
    @Transactional
    public Book updateCoverUrl(UUID bookId, String url) {
        BookEntity entity = jpaBookRepository.findById(bookId).orElseThrow(() -> NotFoundException.bookNotFound(bookId));
        entity.setCoverUrl(url);
        BookEntity updatedBook = jpaBookRepository.save(entity);
        return bookDataMapper.toDomain(updatedBook);
    }
}
