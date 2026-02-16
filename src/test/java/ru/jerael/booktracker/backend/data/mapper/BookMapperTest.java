package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookMapperTest {
    private final GenreMapper genreMapper = new GenreMapper();
    private final BookMapper bookMapper = new BookMapper(genreMapper);

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String title = "title";
    private final String author = "author";
    private final String coverUrl = null;
    private final BookStatus status = BookStatus.READING;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);
    private final Genre genre1 = new Genre(1, "action");
    private final Genre genre2 = new Genre(2, "adventure");
    private final Set<Genre> genres = Set.of(genre1, genre2);
    private final Book book = new Book(id, title, author, coverUrl, status, createdAt, genres);

    @Test
    void toDomain() {
        BookEntity entity = new BookEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setAuthor(author);
        entity.setCoverUrl(coverUrl);
        entity.setStatus(status);
        entity.setCreatedAt(createdAt);
        entity.setGenres(genres.stream().map(genreMapper::toEntity).collect(Collectors.toSet()));

        Book result = bookMapper.toDomain(entity);

        assertEquals(id, result.id());
        assertEquals(title, result.title());
    }

    @Test
    void toEntity() {
        BookEntity entity = bookMapper.toEntity(book);

        assertEquals(id, entity.getId());
        assertEquals(title, entity.getTitle());
    }
}