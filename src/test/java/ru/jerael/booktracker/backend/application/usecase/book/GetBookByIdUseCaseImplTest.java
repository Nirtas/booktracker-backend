package ru.jerael.booktracker.backend.application.usecase.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBookByIdUseCaseImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private GetBookByIdUseCaseImpl useCase;

    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");

    @Test
    void execute_WhenBookExists_ShouldReturnBook() {
        UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
        String title = "title";
        String author = "author";
        BookStatus status = BookStatus.READING;
        Instant createdAt = Instant.ofEpochMilli(1771249699347L);
        Genre genre1 = new Genre(1, "action");
        Genre genre2 = new Genre(2, "adventure");
        Set<Genre> genres = Set.of(genre1, genre2);
        Book book = new Book(
            id,
            title,
            author,
            null,
            status,
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
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));

        Book result = useCase.execute(id, userId);

        assertEquals(book, result);
        verify(bookRepository).findByIdAndUserId(id, userId);
    }

    @Test
    void execute_WhenBookDoesNotExists_ShouldThrowNotFoundException() {
        UUID id = UUID.fromString("12345678-1234-1234-1234-123456789012");
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(id, userId));
    }
}