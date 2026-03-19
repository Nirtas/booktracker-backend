package ru.jerael.booktracker.backend.application.usecase.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import ru.jerael.booktracker.backend.domain.model.image.ImageFile;
import ru.jerael.booktracker.backend.domain.model.image.ProcessedImage;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.service.image.ImageProcessor;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadCoverUseCaseImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCoverStorage bookCoverStorage;

    @Mock
    private ImageProcessor imageProcessor;

    @InjectMocks
    private UploadCoverUseCaseImpl useCase;

    private final UUID id = UUID.fromString("ee39af7a-a073-4473-878a-1aae34e98bb7");
    private final InputStream content = InputStream.nullInputStream();
    private final long contentSize = 0L;

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
    void execute_WhenBookDoesNotExists_ShouldThrowNotFoundException() {
        UploadCover data = new UploadCover("image/jpeg", content, contentSize);
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(id, userId, data));

        verifyNoInteractions(bookCoverStorage);
    }

    @Test
    void execute_WhenContentTypeIsNotAllowed_ShouldThrowValidationException() {
        UploadCover data = new UploadCover("application/json", content, contentSize);
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));

        assertThrows(ValidationException.class, () -> useCase.execute(id, userId, data));

        verifyNoInteractions(bookCoverStorage);
    }

    @Test
    void execute_WhenDataIsValidAndOldCoverDoesNotExists_ShouldSaveCoverAndUpdateFileName() {
        UploadCover data = new UploadCover("image/jpeg", content, contentSize);
        ProcessedImage processedImage = new ProcessedImage("image/jpeg", "jpg", content, contentSize);
        String coverFileName = id + ".jpg";
        ImageFile imageFile = new ImageFile(coverFileName, "image/jpeg", content, contentSize);
        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));
        when(imageProcessor.process(content)).thenReturn(processedImage);
        when(bookRepository.save(any(), eq(userId))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Book result = useCase.execute(id, userId, data);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(coverFileName, result.coverFileName());

        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture(), eq(userId));

        Book capturedBook = bookArgumentCaptor.getValue();
        assertEquals(coverFileName, capturedBook.coverFileName());

        verify(bookCoverStorage).save(imageFile);
        verify(bookRepository).save(any(), eq(userId));
        verify(bookCoverStorage, never()).delete(any());
    }

    @Test
    void execute_WhenOldCoverExists_ShouldDeleteOldCover() {
        String oldCoverFileName = "old_cover.jpg";
        String newCoverFileName = id + ".jpg";
        Book book = new Book(
            id,
            title,
            author,
            oldCoverFileName,
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
        ProcessedImage processedImage = new ProcessedImage("image/jpeg", "jpg", content, contentSize);
        UploadCover data = new UploadCover("image/jpeg", content, contentSize);

        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));
        when(imageProcessor.process(content)).thenReturn(processedImage);
        when(bookRepository.save(any(), eq(userId))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Book result = useCase.execute(id, userId, data);

        assertEquals(newCoverFileName, result.coverFileName());
        verify(bookCoverStorage).delete(oldCoverFileName);
    }

    @Test
    void execute_WhenDeleteOldCoverFails_ShouldReturnUpdatedBook() {
        String oldCoverFileName = "old_cover.jpg";
        Book book = new Book(
            id,
            title,
            author,
            oldCoverFileName,
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
        ProcessedImage processedImage = new ProcessedImage("image/jpeg", "jpg", content, contentSize);
        UploadCover data = new UploadCover("image/jpeg", content, contentSize);

        when(bookRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(book));
        when(imageProcessor.process(content)).thenReturn(processedImage);
        when(bookRepository.save(any(), eq(userId))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        doThrow(new RuntimeException("Error")).when(bookCoverStorage).delete(oldCoverFileName);

        assertDoesNotThrow(() -> useCase.execute(id, userId, data));

        verify(bookRepository).save(any(), eq(userId));
    }
}