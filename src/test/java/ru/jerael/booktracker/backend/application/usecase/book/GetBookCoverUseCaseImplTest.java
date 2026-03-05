package ru.jerael.booktracker.backend.application.usecase.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.image.ImageFile;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBookCoverUseCaseImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCoverStorage bookCoverStorage;

    @InjectMocks
    private GetBookCoverUseCaseImpl useCase;

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final UUID userId = UUID.fromString("2c5781ea-1bc2-4561-a83d-26106df2526e"); // TODO: REMOVE THIS
    private final String title = "title";
    private final String author = "author";
    private final BookStatus status = BookStatus.READING;
    private final Instant createdAt = Instant.ofEpochMilli(1771249699347L);

    @Test
    void execute_WhenCoverExists_ShouldReturnImageFile() {
        byte[] content = "content".getBytes();
        String coverFileName = "cover.jpg";
        ImageFile imageFile = new ImageFile(
            coverFileName,
            "image/jpeg",
            new ByteArrayInputStream(content),
            content.length
        );
        Book book = new Book(id, title, author, coverFileName, status, createdAt, Collections.emptySet());
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));
        when(bookCoverStorage.download(coverFileName)).thenReturn(imageFile);

        ImageFile result = useCase.execute(id, userId);

        assertEquals(imageFile, result);
        verify(bookRepository).findByIdAndUserId(id, userId);
        verify(bookCoverStorage).download(coverFileName);
    }

    @Test
    void execute_WhenBookDoesNotExists_ShouldThrowNotFoundException() {
        UUID id = UUID.fromString("12345678-1234-1234-1234-123456789012");
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(id, userId));
    }

    @Test
    void execute_WhenCoverDoesNotExists_ShouldThrowNotFoundException() {
        Book book = new Book(id, title, author, null, status, createdAt, Collections.emptySet());
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));

        assertThrows(NotFoundException.class, () -> useCase.execute(id, userId));

        verify(bookRepository).findByIdAndUserId(id, userId);
        verify(bookCoverStorage, never()).download(any());
    }
}