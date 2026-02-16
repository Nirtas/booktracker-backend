package ru.jerael.booktracker.backend.data.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class BookMapper {
    private final GenreMapper genreMapper;

    public BookMapper(GenreMapper genreMapper) {
        this.genreMapper = genreMapper;
    }

    public Book toDomain(BookEntity entity) {
        if (entity == null) return null;

        return new Book(
            entity.getId(),
            entity.getTitle(),
            entity.getAuthor(),
            entity.getCoverUrl(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getGenres() == null ? Collections.emptySet() :
                entity.getGenres().stream().map(genreMapper::toDomain).collect(Collectors.toSet())
        );
    }

    public BookEntity toEntity(Book book) {
        if (book == null) return null;

        BookEntity entity = new BookEntity();
        entity.setId(book.id());
        entity.setTitle(book.title());
        entity.setAuthor(book.author());
        entity.setCoverUrl(book.coverUrl());
        entity.setStatus(book.status());
        entity.setCreatedAt(book.createdAt());
        entity.setGenres(book.genres() == null ? Collections.emptySet() :
            book.genres().stream().map(genreMapper::toEntity).collect(Collectors.toSet()));
        return entity;
    }
}
