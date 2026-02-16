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
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}