package ru.jerael.booktracker.backend.web.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.web.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.web.dto.book.BookDetailsUpdateRequest;
import ru.jerael.booktracker.backend.web.dto.book.BookResponse;
import ru.jerael.booktracker.backend.web.util.LinkBuilder;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookWebMapperTest {
    private final GenreWebMapper genreWebMapper = new GenreWebMapper();
    private final AuthorWebMapper authorWebMapper = new AuthorWebMapper();
    private final PublisherWebMapper publisherWebMapper = new PublisherWebMapper();
    private final LanguageWebMapper languageWebMapper = new LanguageWebMapper();
    private final ReadingSessionWebMapper readingSessionWebMapper = new ReadingSessionWebMapper();
    private final ReadingAttemptWebMapper readingAttemptWebMapper =
        new ReadingAttemptWebMapper(readingSessionWebMapper);
    private final NoteWebMapper noteWebMapper = new NoteWebMapper();
    private final LinkBuilder linkBuilder = mock(LinkBuilder.class);
    private final BookWebMapper bookWebMapper =
        new BookWebMapper(genreWebMapper, authorWebMapper, publisherWebMapper, languageWebMapper,
            readingAttemptWebMapper, noteWebMapper, linkBuilder);

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String title = "title";
    private final String coverFileName = "cover.jpg";
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);
    private final Genre genre1 = new Genre(1, "action");
    private final Genre genre2 = new Genre(2, "adventure");
    private final Set<Genre> genres = Set.of(genre1, genre2);
    private final Book book = new Book(
        id,
        userId,
        title,
        coverFileName,
        createdAt,
        genres,
        Set.of(),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        List.of(),
        List.of()
    );

    @Test
    void toResponse() {
        String coverUrl = "http://localhost:8080/api/v1/books/" + id + "/cover";
        when(linkBuilder.buildCoverUrl(id)).thenReturn(coverUrl);

        BookResponse bookResponse = bookWebMapper.toResponse(book);

        assertEquals(id, bookResponse.id());
        assertEquals(title, bookResponse.title());
        assertEquals(coverUrl, bookResponse.coverUrl());
        assertEquals(BookStatus.defaultStatus().getValue(), bookResponse.status());
        assertEquals(createdAt, bookResponse.createdAt());
        assertTrue(bookResponse.genres().containsAll(genreWebMapper.toResponses(genres)));
    }

    @Test
    void toResponses() {
        UUID id2 = UUID.fromString("31d3f5e3-7faf-4678-a3cf-4657d8875a82");
        Book book2 = new Book(
            id2,
            userId,
            "asd",
            coverFileName,
            createdAt,
            genres,
            Set.of(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            List.of()
        );
        List<Book> books = List.of(book, book2);
        when(linkBuilder.buildCoverUrl(id)).thenReturn("url1");
        when(linkBuilder.buildCoverUrl(id2)).thenReturn("url2");

        List<BookResponse> bookResponses = bookWebMapper.toResponses(books);

        assertEquals(2, bookResponses.size());
        assertEquals(id, bookResponses.get(0).id());
        assertEquals(title, bookResponses.get(0).title());
        assertEquals(id2, bookResponses.get(1).id());
        assertEquals("asd", bookResponses.get(1).title());
        assertEquals("url1", bookResponses.get(0).coverUrl());
        assertEquals("url2", bookResponses.get(1).coverUrl());
    }

    @Test
    void toDomain_BookCreation() {
        BookCreationRequest request =
            new BookCreationRequest(" title ", " author ", "reading", Set.of(1, 2));

        BookCreation data = bookWebMapper.toDomain(request);

        assertEquals(request.title().trim(), data.title());
        assertEquals(request.author().trim(), data.author());
        assertEquals(BookStatus.fromString(request.status()), data.status());
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

        BookDetailsUpdate data = bookWebMapper.toDomain(request);

        assertEquals(request.title(), data.title());
        assertEquals(request.author(), data.author());
        assertTrue(data.genreIds().containsAll(request.genreIds()));
    }
}