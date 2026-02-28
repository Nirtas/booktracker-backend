package ru.jerael.booktracker.backend.data.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookDataMapper {
    private final GenreDataMapper genreDataMapper;

    public BookDataMapper(GenreDataMapper genreDataMapper) {
        this.genreDataMapper = genreDataMapper;
    }

    public Book toDomain(BookEntity entity) {
        if (entity == null) return null;

        return new Book(
            entity.getId(),
            entity.getTitle(),
            entity.getAuthor(),
            entity.getCoverFileName(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getGenres() == null ? Collections.emptySet() :
                entity.getGenres().stream().map(genreDataMapper::toDomain).collect(Collectors.toSet())
        );
    }

    public BookEntity toEntity(Book book) {
        if (book == null) return null;

        BookEntity entity = new BookEntity();
        entity.setId(book.id());
        entity.setTitle(book.title());
        entity.setAuthor(book.author());
        entity.setCoverFileName(book.coverFileName());
        entity.setStatus(book.status());
        entity.setCreatedAt(book.createdAt());
        entity.setGenres(book.genres() == null ? Collections.emptySet() :
            book.genres().stream().map(genreDataMapper::toEntity).collect(Collectors.toSet()));
        return entity;
    }

    public BookEntity toEntity(BookCreation data, Set<GenreEntity> genres) {
        if (data == null) return null;

        BookEntity entity = new BookEntity();
        entity.setTitle(data.title());
        entity.setAuthor(data.author());
        entity.setGenres(genres);
        return entity;
    }
}
