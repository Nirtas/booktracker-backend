package ru.jerael.booktracker.backend.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jerael.booktracker.backend.api.dto.book.BookResponse;
import ru.jerael.booktracker.backend.api.mapper.BookApiMapper;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBookByIdUseCase;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBooksUseCase;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {
    private final GetBooksUseCase getBooksUseCase;
    private final GetBookByIdUseCase getBookByIdUseCase;
    private final BookApiMapper bookApiMapper;

    @GetMapping
    public List<BookResponse> getAll() {
        List<Book> books = getBooksUseCase.execute();
        return bookApiMapper.toResponses(books);
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable UUID id) {
        Book book = getBookByIdUseCase.execute(id);
        return bookApiMapper.toResponse(book);
    }
}
