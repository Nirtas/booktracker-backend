package ru.jerael.booktracker.backend.application.usecase.book;

import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBooksUseCase;
import java.util.List;

@Service
public class GetBooksUseCaseImpl implements GetBooksUseCase {
    private final BookRepository bookRepository;

    public GetBooksUseCaseImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> execute() {
        return bookRepository.getBooks();
    }
}
