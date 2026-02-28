package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import ru.jerael.booktracker.backend.domain.usecase.book.DeleteBookUseCase;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteBookUseCaseImpl implements DeleteBookUseCase {
    private final BookRepository bookRepository;
    private final BookCoverStorage bookCoverStorage;

    @Override
    @Transactional
    public void execute(UUID id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> BookExceptionFactory.notFound(id));
        String coverFileName = book.coverFileName();
        bookRepository.deleteById(id);
        if (coverFileName != null) {
            bookCoverStorage.delete(coverFileName);
        }
    }
}
