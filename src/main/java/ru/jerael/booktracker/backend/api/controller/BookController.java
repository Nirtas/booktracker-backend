package ru.jerael.booktracker.backend.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.jerael.booktracker.backend.api.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookResponse;
import ru.jerael.booktracker.backend.api.mapper.BookApiMapper;
import ru.jerael.booktracker.backend.api.mapper.UploadCoverApiMapper;
import ru.jerael.booktracker.backend.api.validator.FileValidator;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import ru.jerael.booktracker.backend.domain.usecase.book.CreateBookUseCase;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBookByIdUseCase;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBooksUseCase;
import ru.jerael.booktracker.backend.domain.usecase.book.UploadCoverUseCase;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {
    private final GetBooksUseCase getBooksUseCase;
    private final GetBookByIdUseCase getBookByIdUseCase;
    private final CreateBookUseCase createBookUseCase;
    private final UploadCoverUseCase uploadCoverUseCase;
    private final FileValidator fileValidator;
    private final BookApiMapper bookApiMapper;
    private final UploadCoverApiMapper uploadCoverApiMapper;

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

    @PatchMapping("/{id}/cover")
    public BookResponse uploadCover(@PathVariable UUID id, @RequestParam("cover") MultipartFile file) {
        fileValidator.validate(file, "cover");
        UploadCover data = uploadCoverApiMapper.toDomain(id, file);
        Book book = uploadCoverUseCase.execute(data);
        return bookApiMapper.toResponse(book);
    }
}
