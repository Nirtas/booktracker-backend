package ru.jerael.booktracker.backend.api.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.api.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookDetailsUpdateRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookResponse;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookApiMapperTest {
    private final GenreApiMapper genreApiMapper = new GenreApiMapper();
    private final BookApiMapper bookApiMapper = new BookApiMapper(genreApiMapper);

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String title = "title";
    private final String author = "author";
    private final String coverFileName = "cover.jpg";
    private final BookStatus status = BookStatus.READING;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);
    private final Genre genre1 = new Genre(1, "action");
    private final Genre genre2 = new Genre(2, "adventure");
    private final Set<Genre> genres = Set.of(genre1, genre2);
    private final Book book = new Book(id, title, author, coverFileName, status, createdAt, genres);

    @Test
    void toResponse() {
        String coverUrl = coverFileName;

        BookResponse bookResponse = bookApiMapper.toResponse(book);

        assertEquals(id, bookResponse.id());
        assertEquals(title, bookResponse.title());
        assertEquals(author, bookResponse.author());
        assertEquals(coverUrl, bookResponse.coverUrl());
        assertEquals(status.getValue(), bookResponse.status());
        assertEquals(createdAt, bookResponse.createdAt());
        assertTrue(bookResponse.genres().containsAll(genreApiMapper.toResponses(genres)));
    }

    @Test
    void toResponses() {
        UUID id2 = UUID.fromString("31d3f5e3-7faf-4678-a3cf-4657d8875a82");
        Book book2 = new Book(id2, "asd", author, coverFileName, status, createdAt, genres);
        List<Book> books = List.of(book, book2);

        List<BookResponse> bookResponses = bookApiMapper.toResponses(books);

        assertEquals(2, bookResponses.size());
        assertEquals(id, bookResponses.get(0).id());
        assertEquals(title, bookResponses.get(0).title());
        assertEquals(id2, bookResponses.get(1).id());
        assertEquals("asd", bookResponses.get(1).title());
    }

    @Test
    void toDomain_BookCreation() {
        BookCreationRequest request = new BookCreationRequest(" title ", " author ", Set.of(1, 2));

        BookCreation data = bookApiMapper.toDomain(request);

        assertEquals(request.title().trim(), data.title());
        assertEquals(request.author().trim(), data.author());
        assertTrue(data.genreIds().containsAll(request.genreIds()));
    }

    @Test
    void toDomain_BookDetailsUpdate() {
        BookDetailsUpdateRequest request = new BookDetailsUpdateRequest(
            " title ",
            " author ",
            "reading",
            Set.of(1, 2)
        );

        BookDetailsUpdate data = bookApiMapper.toDomain(request);

        assertEquals(request.title(), data.title());
        assertEquals(request.author(), data.author());
        assertTrue(data.genreIds().containsAll(request.genreIds()));
    }
}