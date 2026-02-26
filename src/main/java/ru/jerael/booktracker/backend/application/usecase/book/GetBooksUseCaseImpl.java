package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBooksUseCase;

@Service
@RequiredArgsConstructor
public class GetBooksUseCaseImpl implements GetBooksUseCase {
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<Book> execute(PageQuery query) {
        return bookRepository.findAll(query);
    }
}
