package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.image.ImageFile;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.storage.BookCoverStorage;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBookCoverUseCase;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class GetBookCoverUseCaseImpl implements GetBookCoverUseCase {
    private final BookRepository bookRepository;
    private final BookCoverStorage bookCoverStorage;

    @Override
    @Transactional(readOnly = true)
    public ImageFile execute(UUID id, UUID userId) {
        Book book =
            bookRepository.findByIdAndUserId(id, userId).orElseThrow(() -> BookExceptionFactory.bookNotFound(id));
        if (book.coverFileName() == null || book.coverFileName().isBlank()) {
            throw BookExceptionFactory.coverNotFound(id);
        }
        return bookCoverStorage.download(book.coverFileName());
    }
}
