package ru.jerael.booktracker.backend.data.mapper;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookDataMapperTest {
    private final GenreDataMapper genreDataMapper = new GenreDataMapper();
    private final AuthorDataMapper authorDataMapper = new AuthorDataMapper();
    private final PublisherDataMapper publisherDataMapper = new PublisherDataMapper();
    private final LanguageDataMapper languageDataMapper = new LanguageDataMapper();
    private final ReadingSessionDataMapper readingSessionDataMapper = new ReadingSessionDataMapper();
    private final ReadingAttemptDataMapper readingAttemptDataMapper =
        new ReadingAttemptDataMapper(readingSessionDataMapper);
    private final NoteDataMapper noteDataMapper = new NoteDataMapper();
    private final BookDataMapper bookDataMapper = new BookDataMapper(
        genreDataMapper, authorDataMapper, publisherDataMapper,
        languageDataMapper, readingAttemptDataMapper, noteDataMapper
    );

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String title = "title";
    private final String coverFileName = null;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);
    private final Genre genre1 = new Genre(1, "action");
    private final Genre genre2 = new Genre(2, "adventure");
    private final Set<Genre> genres = Set.of(genre1, genre2);
    private final Book book = new Book(
        id,
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
    void entity_toDomain() {
        BookEntity entity = new BookEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setCoverFileName(coverFileName);
        entity.setCreatedAt(createdAt);
        entity.setGenres(genres.stream().map(genreDataMapper::toEntity).collect(Collectors.toSet()));

        Book result = bookDataMapper.toDomain(entity);

        assertEquals(id, result.id());
        assertEquals(title, result.title());
    }

    @Test
    void domain_toEntity() {
        BookEntity entity = bookDataMapper.toEntity(book);

        assertEquals(id, entity.getId());
        assertEquals(title, entity.getTitle());
    }
}