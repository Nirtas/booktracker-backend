package ru.jerael.booktracker.backend.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.jerael.booktracker.backend.api.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookResponse;
import ru.jerael.booktracker.backend.api.mapper.BookApiMapper;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.usecase.book.CreateBookUseCase;
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
    private final CreateBookUseCase createBookUseCase;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody BookCreationRequest request) {
        BookCreation data = bookApiMapper.toDomain(request);
        Book book = createBookUseCase.execute(data);
        return bookApiMapper.toResponse(book);
    }
}
