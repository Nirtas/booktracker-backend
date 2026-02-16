package ru.jerael.booktracker.backend.application.usecase.book;

import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBookByIdUseCase;
import java.util.UUID;

@Service
public class GetBookByIdUseCaseImpl implements GetBookByIdUseCase {
    private final BookRepository bookRepository;

    public GetBookByIdUseCaseImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book execute(UUID id) {
        return bookRepository.getBookById(id).orElseThrow(() -> NotFoundException.bookNotFound(id));
    }
}
