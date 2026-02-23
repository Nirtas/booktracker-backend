package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBookByIdUseCase;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetBookByIdUseCaseImpl implements GetBookByIdUseCase {
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Book execute(UUID id) {
        return bookRepository.findById(id).orElseThrow(() -> NotFoundException.bookNotFound(id));
    }
}
