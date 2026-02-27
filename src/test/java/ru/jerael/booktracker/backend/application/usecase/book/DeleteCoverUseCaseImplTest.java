package ru.jerael.booktracker.backend.application.usecase.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCoverUseCaseImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCoverStorage bookCoverStorage;

    @InjectMocks
    private DeleteCoverUseCaseImpl useCase;

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final String title = "title";
    private final String author = "author";
    private final BookStatus status = BookStatus.WANT_TO_READ;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    @Test
    void execute_WhenBookDoesNotExists_ShouldThrowNotFoundException() {
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(id));

        verifyNoInteractions(bookCoverStorage);
    }

    @Test
    void execute_WhenCoverExists_ShouldDeleteFromStorageAndSaveBookWithNullCover() {
        String coverUrl = "cover.jpg";
        Book book = new Book(id, title, author, coverUrl, status, createdAt, Collections.emptySet());
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        useCase.execute(id);

        Book updatedBook = new Book(id, title, author, null, status, createdAt, Collections.emptySet());
        verify(bookCoverStorage).delete(coverUrl);
        verify(bookRepository).save(updatedBook);
    }

    @Test
    void execute_WhenCoverDoesNotExists_ShouldExitWithoutUpdate() {
        Book book = new Book(id, title, author, null, status, createdAt, Collections.emptySet());
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        useCase.execute(id);

        verify(bookCoverStorage, never()).delete(any());
        verify(bookRepository, never()).save(any());
    }
}