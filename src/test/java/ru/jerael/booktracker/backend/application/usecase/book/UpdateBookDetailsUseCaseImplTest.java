package ru.jerael.booktracker.backend.application.usecase.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.repository.GenreRepository;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBookDetailsUseCaseImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private UpdateBookDetailsUseCaseImpl useCase;

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String title = "title";
    private final String author = "author";
    private final BookStatus status = BookStatus.WANT_TO_READ;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);
    private final Book book = new Book(
        id,
        title,
        author,
        null,
        status,
        createdAt,
        Collections.emptySet(),
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
    void execute_WhenGenreNotFound_ShouldThrowException() {
        Set<Integer> genreIds = Set.of(1, 5555);
        BookDetailsUpdate data = new BookDetailsUpdate(null, null, null, genreIds);
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));
        when(genreRepository.findAllById(genreIds)).thenReturn(Set.of(new Genre(1, "action")));

        String message = assertThrows(NotFoundException.class, () -> useCase.execute(id, userId, data)).getMessage();

        assertThat(message).contains("5555");
    }

    @Test
    void execute_ShouldUpdateOnlyProvidedFields() {
        Genre genre = new Genre(1, "action");
        BookDetailsUpdate data = new BookDetailsUpdate(" new title ", null, null, Set.of(1));
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));
        when(genreRepository.findAllById(Set.of(1))).thenReturn(Set.of(genre));
        when(bookRepository.save(any(), eq(userId))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Book updatedBook = useCase.execute(id, userId, data);

        assertEquals("new title", updatedBook.title());
        assertEquals(author, updatedBook.author());
        assertEquals(status, updatedBook.status());
        assertThat(updatedBook.genres()).containsExactly(genre);
        verify(bookRepository).save(any(), eq(userId));
    }
}