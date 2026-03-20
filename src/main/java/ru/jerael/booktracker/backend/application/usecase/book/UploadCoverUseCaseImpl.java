package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.constant.BookRules;
import ru.jerael.booktracker.backend.domain.constant.ImageRules;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.exception.factory.FileValidationExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import ru.jerael.booktracker.backend.domain.model.image.ImageFile;
import ru.jerael.booktracker.backend.domain.model.image.ProcessedImage;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.service.image.ImageProcessor;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import ru.jerael.booktracker.backend.domain.usecase.book.UploadCoverUseCase;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadCoverUseCaseImpl implements UploadCoverUseCase {
    private static final Logger log = LoggerFactory.getLogger(UploadCoverUseCaseImpl.class);
    private final BookRepository bookRepository;
    private final BookCoverStorage bookCoverStorage;
    private final ImageProcessor imageProcessor;

    @Override
    @Transactional
    public Book execute(UUID bookId, UUID userId, UploadCover data) {
        Book book =
            bookRepository.findByIdAndUserId(bookId, userId)
                .orElseThrow(() -> BookExceptionFactory.bookNotFound(bookId));
        if (!ImageRules.ALLOWED_MIME_TYPES.contains(data.contentType())) {
            throw FileValidationExceptionFactory.unsupportedFileContentType(
                data.contentType(),
                BookRules.COVER_FIELD_NAME
            );
        }
        String oldCoverFileName = book.coverFileName();

        ProcessedImage processedImage = imageProcessor.process(data.content());
        String newCoverFileName = bookId + "." + processedImage.extension();
        ImageFile imageFile = new ImageFile(
            newCoverFileName,
            processedImage.contentType(),
            processedImage.content(),
            processedImage.size()
        );
        bookCoverStorage.save(imageFile);

        Book updatedBook = new Book(
            book.id(),
            book.title(),
            newCoverFileName,
            book.createdAt(),
            book.genres(),
            book.authors(),
            book.description(),
            book.publisher(),
            book.language(),
            book.publishedOn(),
            book.totalPages(),
            book.isbn10(),
            book.isbn13(),
            book.attempts(),
            book.notes()
        );
        Book savedBook = bookRepository.save(updatedBook, userId);
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
