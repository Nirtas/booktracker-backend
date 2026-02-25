package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.constants.BookRules;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.exception.factory.FileValidationExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import ru.jerael.booktracker.backend.domain.usecase.book.UploadCoverUseCase;

@Service
@RequiredArgsConstructor
public class UploadCoverUseCaseImpl implements UploadCoverUseCase {
    private final BookRepository bookRepository;
    private final BookCoverStorage bookCoverStorage;

    @Override
    @Transactional
    public Book execute(UploadCover data) {
        Book book =
            bookRepository.findById(data.bookId()).orElseThrow(() -> BookExceptionFactory.notFound(data.bookId()));
        if (!BookRules.ALLOWED_IMAGE_MIME_TYPES.contains(data.contentType())) {
            throw FileValidationExceptionFactory.unsupportedFileContentType(data.contentType(), "cover");
        }
        String newCoverUrl = bookCoverStorage.save(data.bookId(), data.contentType(), data.content());
        Book updatedBook = new Book(
            book.id(),
            book.title(),
            book.author(),
            newCoverUrl,
            book.status(),
            book.createdAt(),
            book.genres()
        );
        return bookRepository.save(updatedBook);
    }
}
