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
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
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
    void getBooks_ShouldReturnAllBooks() {
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

        List<Book> result = bookRepository.getBooks();

        assertEquals(2, result.size());
        assertThat(result).extracting(Book::id).containsExactlyInAnyOrder(id1, id2);
    }

    @Test
    void getBookById_WhenExists_ShouldReturnBook() {
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

        Optional<Book> result = bookRepository.getBookById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().id());
        assertEquals("action", result.get().genres().iterator().next().name());
    }

    @Test
    void create_ShouldCreateBookWithExistingGenres() {
        GenreEntity genre1 = new GenreEntity();
        genre1.setName("action");
        GenreEntity genre2 = new GenreEntity();
        genre2.setName("adventure");
        List<GenreEntity> genreEntities = jpaGenreRepository.saveAll(Set.of(genre1, genre2));

        Set<Integer> genreIds = genreEntities.stream().map(GenreEntity::getId).collect(Collectors.toSet());
        Set<Genre> genres = genreEntities.stream()
            .map(entity -> new Genre(entity.getId(), entity.getName()))
            .collect(Collectors.toSet());

        String title = "title";
        String author = "author";
        BookCreation data = new BookCreation(title, author, genreIds);

        Book createdBook = bookRepository.create(data, genres);
        assertNotNull(createdBook.id());
        assertEquals(title, createdBook.title());
        assertEquals(author, createdBook.author());
        assertNull(createdBook.coverUrl());
        assertEquals(BookStatus.WANT_TO_READ, createdBook.status());
        assertNotNull(createdBook.createdAt());
        assertEquals(2, createdBook.genres().size());
        assertThat(createdBook.genres()).extracting(Genre::id).containsExactlyInAnyOrder(genre1.getId(), genre2.getId());

        Optional<Book> result = bookRepository.getBookById(createdBook.id());
        assertTrue(result.isPresent());
        assertEquals(2, createdBook.genres().size());
    }

    @Test
    void create_ShouldCreateBookWithoutGenres() {
        String title = "title";
        String author = "author";
        BookCreation data = new BookCreation(title, author, Collections.emptySet());
        Set<Genre> genres = Collections.emptySet();

        Book createdBook = bookRepository.create(data, genres);
        assertNotNull(createdBook.id());
        assertEquals(title, createdBook.title());
        assertEquals(author, createdBook.author());
        assertNull(createdBook.coverUrl());
        assertEquals(BookStatus.WANT_TO_READ, createdBook.status());
        assertNotNull(createdBook.createdAt());
        assertTrue(createdBook.genres().isEmpty());

        Optional<Book> result = bookRepository.getBookById(createdBook.id());
        assertTrue(result.isPresent());
        assertTrue(result.get().genres().isEmpty());
    }

    @Test
    void updateCoverUrl_ShouldUpdateExistingBookCover() {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle("title");
        bookEntity.setAuthor("author");
        bookEntity.setCoverUrl("old_cover.jpg");
        bookEntity.setStatus(BookStatus.READING);
        bookEntity.setCreatedAt(Instant.ofEpochMilli(1771249699347L));
        bookEntity.setGenres(Collections.emptySet());

        BookEntity savedBook = jpaBookRepository.save(bookEntity);
        UUID id = savedBook.getId();

        String newCoverUrl = "new_cover.jpg";

        Book updatedBook = bookRepository.updateCoverUrl(id, newCoverUrl);
        assertEquals(id, updatedBook.id());
        assertEquals(newCoverUrl, updatedBook.coverUrl());

        Optional<Book> result = bookRepository.getBookById(id);
        assertTrue(result.isPresent());
        assertEquals(newCoverUrl, result.get().coverUrl());
    }

    @Test
    void updateCoverUrl_ShouldSetCoverUrlToNull() {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle("title");
        bookEntity.setAuthor("author");
        bookEntity.setCoverUrl("old_cover.jpg");
        bookEntity.setStatus(BookStatus.READING);
        bookEntity.setCreatedAt(Instant.ofEpochMilli(1771249699347L));
        bookEntity.setGenres(Collections.emptySet());

        BookEntity savedBook = jpaBookRepository.save(bookEntity);
        UUID id = savedBook.getId();

        Book updatedBook = bookRepository.updateCoverUrl(id, null);
        assertEquals(id, updatedBook.id());
        assertNull(updatedBook.coverUrl());

        Optional<Book> result = bookRepository.getBookById(id);
        assertTrue(result.isPresent());
        assertNull(result.get().coverUrl());
    }

    @Test
    void updateCoverUrl_WhenBookDoesNotExists_ShouldThrowNotFoundException() {
        UUID id = UUID.fromString("139a383e-9de1-4378-9479-64949da3d982");
        String url = "test.jpg";

        assertThrows(NotFoundException.class, () -> bookRepository.updateCoverUrl(id, url));

        assertThat(bookRepository.getBooks().isEmpty());
    }
}