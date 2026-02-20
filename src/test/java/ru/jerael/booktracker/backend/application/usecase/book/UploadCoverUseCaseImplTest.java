package ru.jerael.booktracker.backend.application.usecase.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadCoverUseCaseImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCoverStorage bookCoverStorage;

    @InjectMocks
    private UploadCoverUseCaseImpl useCase;

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final InputStream content = InputStream.nullInputStream();

    private final String title = "title";
    private final String author = "author";
    private final BookStatus status = BookStatus.WANT_TO_READ;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);
    private final Book book = new Book(id, title, author, null, status, createdAt, Collections.emptySet());

    @Test
    void execute_WhenBookDoesNotExists_ShouldThrowNotFoundException() {
        UploadCover data = new UploadCover(id, "cover.jpg", "image/jpeg", content);
        when(bookRepository.getBookById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(data));

        verifyNoInteractions(bookCoverStorage);
    }

    @Test
    void execute_WhenContentTypeIsNotAllowed_ShouldThrowValidationException() {
        UploadCover data = new UploadCover(id, "cover.jpg", "application/json", content);
        when(bookRepository.getBookById(id)).thenReturn(Optional.of(book));

        assertThrows(ValidationException.class, () -> useCase.execute(data));

        verifyNoInteractions(bookCoverStorage);
    }

    @Test
    void execute_WhenDataIsValid_ShouldSaveCoverAndUpdateUrl() {
        UploadCover data = new UploadCover(id, "cover.jpg", "image/jpeg", content);
        String path = "covers/cover.jpg";
        Book updatedBook = new Book(id, title, author, path, status, createdAt, Collections.emptySet());
        when(bookRepository.getBookById(id)).thenReturn(Optional.of(book));
        when(bookCoverStorage.save(id, "image/jpeg", content)).thenReturn(path);
        when(bookRepository.updateCoverUrl(id, path)).thenReturn(updatedBook);

        Book result = useCase.execute(data);

        assertNotNull(result);
        assertEquals(path, result.coverUrl());
        verify(bookCoverStorage).save(id, "image/jpeg", content);
        verify(bookRepository).updateCoverUrl(id, path);
    }
}