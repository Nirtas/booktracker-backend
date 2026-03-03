package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import ru.jerael.booktracker.backend.domain.usecase.book.DeleteCoverUseCase;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCoverUseCaseImpl implements DeleteCoverUseCase {
    private final BookRepository bookRepository;
    private final BookCoverStorage bookCoverStorage;

    @Override
    @Transactional
    public void execute(UUID bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> BookExceptionFactory.bookNotFound(bookId));
        if (book.coverFileName() == null) return;

        Book updatedBook = new Book(
            book.id(),
            book.title(),
            book.author(),
            null,
            book.status(),
            book.createdAt(),
            book.genres()
        );
        bookRepository.save(updatedBook);
        bookCoverStorage.delete(book.coverFileName());
    }
}
