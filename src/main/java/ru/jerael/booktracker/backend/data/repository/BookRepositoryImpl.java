package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository;
import ru.jerael.booktracker.backend.data.mapper.BookDataMapper;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final JpaBookRepository jpaBookRepository;
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
    public Book save(Book book) {
        BookEntity entity = bookDataMapper.toEntity(book);
        BookEntity savedEntity = jpaBookRepository.save(entity);
        return bookDataMapper.toDomain(savedEntity);
    }
}
