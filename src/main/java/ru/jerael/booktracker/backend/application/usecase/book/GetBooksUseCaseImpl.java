package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBooksUseCase;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBooksUseCaseImpl implements GetBooksUseCase {
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Book> execute() {
        return bookRepository.findAll();
    }
}
