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
    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e");
    private final String title = "title";
    private final String author = "author";
    private final BookStatus status = BookStatus.WANT_TO_READ;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    @Test
    void execute_WhenBookDoesNotExists_ShouldThrowNotFoundException() {
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(id, userId));

        verifyNoInteractions(bookCoverStorage);
    }

    @Test
    void execute_WhenCoverExists_ShouldDeleteFromStorageAndSaveBookWithNullCover() {
        String coverFileName = "cover.jpg";
        Book book = new Book(id, title, author, coverFileName, status, createdAt, Collections.emptySet());
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));

        useCase.execute(id, userId);

        Book updatedBook = new Book(id, title, author, null, status, createdAt, Collections.emptySet());
        verify(bookCoverStorage).delete(coverFileName);
        verify(bookRepository).save(updatedBook, userId);
    }

    @Test
    void execute_WhenCoverDoesNotExists_ShouldExitWithoutUpdate() {
        Book book = new Book(id, title, author, null, status, createdAt, Collections.emptySet());
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));

        useCase.execute(id, userId);

        verify(bookCoverStorage, never()).delete(any());
        verify(bookRepository, never()).save(any(), eq(userId));
    }
}