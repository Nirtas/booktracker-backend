package ru.jerael.booktracker.backend.application.usecase.book;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.jerael.booktracker.backend.application.annotation.UseCase;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.repository.BookRepository;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBooksUseCase;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class GetBooksUseCaseImpl implements GetBooksUseCase {
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<Book> execute(PageQuery query, UUID userId) {
        return bookRepository.findAllByUserId(query, userId);
    }
}
