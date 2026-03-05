package ru.jerael.booktracker.backend.application.usecase.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBookUseCaseImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private CreateBookUseCaseImpl useCase;

    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e"); // TODO: REMOVE THIS

    @Test
    void execute_WhenAllGenresFound_ShouldCreateBookWithAllGenres() {
        UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");

        String title = "title";
        String author = "author";
        BookStatus status = BookStatus.READING;
        Set<Integer> genreIds = Set.of(1, 2);
        BookCreation data = new BookCreation(title, author, status, genreIds);

        Genre genre1 = new Genre(1, "action");
        Genre genre2 = new Genre(2, "adventure");
        Set<Genre> genres = Set.of(genre1, genre2);

        when(genreRepository.findAllById(genreIds)).thenReturn(genres);
        when(bookRepository.save(any(), eq(userId))).thenAnswer(invocationOnMock -> {
            Book book = invocationOnMock.getArgument(0);
            return new Book(
                id,
                book.title(),
                book.author(),
                book.coverFileName(),
                book.status(),
                book.createdAt(),
                book.genres()
            );
        });

        Book result = useCase.execute(data, userId);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertNotNull(result.createdAt());
        assertEquals(status, result.status());
        assertEquals(genres, result.genres());

        verify(genreRepository).findAllById(genreIds);

        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture(), eq(userId));

        Book capturedBook = bookArgumentCaptor.getValue();
        assertNull(capturedBook.id());
        assertTrue(capturedBook.createdAt().isBefore(Instant.now().plusSeconds(2)));
    }

    @Test
    void execute_WhenOneOrMoreGenresNotFound_ShouldThrowNotFoundException() {
        String title = "title";
        String author = "author";
        Set<Integer> genreIds = Set.of(1, 2);
        BookCreation data = new BookCreation(title, author, null, genreIds);

        Genre genre1 = new Genre(1, "action");
        when(genreRepository.findAllById(genreIds)).thenReturn(Set.of(genre1));

        assertThrows(NotFoundException.class, () -> useCase.execute(data, userId));
        verify(bookRepository, never()).save(any(), eq(userId));
    }
}