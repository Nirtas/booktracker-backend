package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.exception.factory.BookExceptionFactory;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBookByIdUseCase;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class GetBookByIdUseCaseImpl implements GetBookByIdUseCase {
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Book execute(UUID id, UUID userId) {
        return bookRepository.findByIdAndUserId(id, userId).orElseThrow(() -> BookExceptionFactory.bookNotFound(id));
    }
}
