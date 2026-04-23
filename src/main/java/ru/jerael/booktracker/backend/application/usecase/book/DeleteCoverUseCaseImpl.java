package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import ru.jerael.booktracker.backend.domain.usecase.book.DeleteCoverUseCase;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class DeleteCoverUseCaseImpl implements DeleteCoverUseCase {
    private final BookRepository bookRepository;
    private final BookCoverStorage bookCoverStorage;

    @Override
    @Transactional
    public void execute(UUID bookId, UUID userId) {
        Book book = bookRepository.findByIdAndUserId(bookId, userId)
            .orElseThrow(() -> BookExceptionFactory.bookNotFound(bookId));
        if (book.coverFileName() == null) return;

        Book updatedBook = new Book(
            book.id(),
            userId,
            book.title(),
            null,
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
        bookRepository.save(updatedBook, userId);
        bookCoverStorage.delete(book.coverFileName());
    }
}
