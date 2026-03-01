package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.constant.BookRules;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.exception.factory.FileValidationExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import ru.jerael.booktracker.backend.domain.usecase.book.UploadCoverUseCase;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadCoverUseCaseImpl implements UploadCoverUseCase {
    private static final Logger log = LoggerFactory.getLogger(UploadCoverUseCaseImpl.class);
    private final BookRepository bookRepository;
    private final BookCoverStorage bookCoverStorage;

    @Override
    @Transactional
    public Book execute(UUID bookId, UploadCover data) {
        Book book =
            bookRepository.findById(bookId).orElseThrow(() -> BookExceptionFactory.notFound(bookId));
        if (!BookRules.ALLOWED_IMAGE_MIME_TYPES.contains(data.contentType())) {
            throw FileValidationExceptionFactory.unsupportedFileContentType(data.contentType(), "cover");
        }
        String oldCoverFileName = book.coverFileName();
        String newCoverFileName = bookCoverStorage.save(bookId, data);
        Book updatedBook = new Book(
            book.id(),
            book.title(),
            book.author(),
            newCoverFileName,
            book.status(),
            book.createdAt(),
            book.genres()
        );
        Book savedBook = bookRepository.save(updatedBook);
        if (oldCoverFileName != null && !oldCoverFileName.equals(newCoverFileName)) {
            try {
                bookCoverStorage.delete(oldCoverFileName);
            } catch (Exception e) {
                log.error("Failed to delete old cover: {}", oldCoverFileName, e);
            }
        }
        return savedBook;
    }
}
