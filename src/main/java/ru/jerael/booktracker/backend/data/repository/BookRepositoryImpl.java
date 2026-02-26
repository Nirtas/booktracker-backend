package ru.jerael.booktracker.backend.data.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository;
import ru.jerael.booktracker.backend.data.mapper.BookDataMapper;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final JpaBookRepository jpaBookRepository;
    private final BookDataMapper bookDataMapper;

    @Override
    public PageResult<Book> findAll(PageQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<BookEntity> entities = jpaBookRepository.findAll(pageable);
        return new PageResult<>(
            entities.getContent().stream().map(bookDataMapper::toDomain).toList(),
            entities.getSize(),
            entities.getNumber(),
            entities.getTotalElements(),
            entities.getTotalPages()
        );
    }

    @Override
    public Optional<Book> findById(UUID id) {
        return jpaBookRepository.findById(id).map(bookDataMapper::toDomain);
    }

    @Override
    public Book save(Book book) {
        BookEntity entity = bookDataMapper.toEntity(book);
        BookEntity savedEntity = jpaBookRepository.save(entity);
        return bookDataMapper.toDomain(savedEntity);
    }
}
