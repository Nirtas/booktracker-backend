package ru.jerael.booktracker.backend.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.jerael.booktracker.backend.data.db.entity.BookEntity;
import ru.jerael.booktracker.backend.data.db.entity.GenreEntity;
import ru.jerael.booktracker.backend.data.db.repository.JpaBookRepository;
import ru.jerael.booktracker.backend.data.db.repository.JpaGenreRepository;
import ru.jerael.booktracker.backend.data.mapper.BookDataMapper;
import ru.jerael.booktracker.backend.data.mapper.GenreDataMapper;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.model.pagination.SortDirection;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({BookRepositoryImpl.class, GenreRepositoryImpl.class, GenreDataMapper.class, BookDataMapper.class})
class BookRepositoryImplTest {

    @Autowired
    private BookRepositoryImpl bookRepository;

    @Autowired
    private JpaBookRepository jpaBookRepository;

    @Autowired
    private JpaGenreRepository jpaGenreRepository;

    @Test
    void findAll_ShouldReturnAllBooks() {
        GenreEntity genre1 = new GenreEntity();
        genre1.setName("action");
        GenreEntity genre2 = new GenreEntity();
        genre2.setName("adventure");
        List<GenreEntity> genres = jpaGenreRepository.saveAll(Set.of(genre1, genre2));

        BookEntity book1 = new BookEntity();
        book1.setTitle("title");
        book1.setAuthor("author");
        book1.setStatus(BookStatus.READING);
        book1.setCreatedAt(Instant.ofEpochMilli(1771249699347L));
        book1.setGenres(new HashSet<>(genres));

        BookEntity book2 = new BookEntity();
        book2.setTitle("asd");
        book2.setAuthor("dsa");
        book2.setStatus(BookStatus.WANT_TO_READ);
        book2.setCreatedAt(Instant.ofEpochMilli(177124961234L));
        book2.setGenres(Collections.emptySet());

        List<BookEntity> entities = jpaBookRepository.saveAll(List.of(book1, book2));
        UUID id1 = entities.get(0).getId();
        UUID id2 = entities.get(1).getId();

        PageQuery query = new PageQuery(0, 10, "createdAt", SortDirection.DESC);

        PageResult<Book> result = bookRepository.findAll(query);

        assertEquals(2, result.content().size());
        assertEquals(10, result.size());
        assertEquals(0, result.number());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
        assertThat(result.content()).extracting(Book::id).containsExactlyInAnyOrder(id1, id2);
    }

    @Test
    void findById_WhenExists_ShouldReturnBook() {
        GenreEntity entity = new GenreEntity();
        entity.setName("action");
        GenreEntity savedGenre = jpaGenreRepository.save(entity);

        BookEntity book1 = new BookEntity();
        book1.setTitle("title");
        book1.setAuthor("author");
        book1.setStatus(BookStatus.READING);
        book1.setCreatedAt(Instant.ofEpochMilli(1771249699347L));
        book1.setGenres(Set.of(savedGenre));

        BookEntity savedBook = jpaBookRepository.save(book1);
        UUID id = savedBook.getId();

        Optional<Book> result = bookRepository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().id());
        assertEquals("action", result.get().genres().iterator().next().name());
    }

    @Test
    void save_WhenIdIsNull_ShouldInsertNewBook() {
        GenreEntity genre1 = new GenreEntity();
        genre1.setName("action");
        GenreEntity genre2 = new GenreEntity();
        genre2.setName("adventure");
        List<GenreEntity> genreEntities = jpaGenreRepository.saveAll(Set.of(genre1, genre2));

        Set<Genre> genres = genreEntities.stream()
            .map(entity -> new Genre(entity.getId(), entity.getName()))
            .collect(Collectors.toSet());

        String title = "title";
        String author = "author";
        Book book = new Book(
            null,
            title,
            author,
            null,
            BookStatus.WANT_TO_READ,
            Instant.now(),
            genres
        );

        Book createdBook = bookRepository.save(book);

        assertNotNull(createdBook.id());
        assertEquals(title, createdBook.title());
        assertEquals(author, createdBook.author());
        assertEquals(2, createdBook.genres().size());
        assertTrue(jpaBookRepository.existsById(createdBook.id()));
    }

    @Test
    void save_WhenIdIsPresent_ShouldUpdateExistingBook() {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle("title");
        bookEntity.setAuthor("author");
        bookEntity.setCoverUrl("old_cover.jpg");
        bookEntity.setStatus(BookStatus.READING);
        bookEntity.setCreatedAt(Instant.ofEpochMilli(1771249699347L));
        bookEntity.setGenres(Collections.emptySet());

        BookEntity savedBook = jpaBookRepository.save(bookEntity);
        UUID id = savedBook.getId();

        Book book = new Book(
            id,
            "new title",
            "new author",
            "new_cover.jpg",
            savedBook.getStatus(),
            savedBook.getCreatedAt(),
            Collections.emptySet()
        );

        Book updatedBook = bookRepository.save(book);

        assertEquals(id, updatedBook.id());
        assertEquals("new_cover.jpg", updatedBook.coverUrl());

        BookEntity entity = jpaBookRepository.findById(id).orElseThrow();
        assertEquals("new title", entity.getTitle());
        assertEquals("new author", entity.getAuthor());
    }
}