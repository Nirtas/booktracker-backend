package ru.jerael.booktracker.backend.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
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
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.usecase.book.CreateBookUseCase;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBookByIdUseCase;
import ru.jerael.booktracker.backend.domain.usecase.book.GetBooksUseCase;
import ru.jerael.booktracker.backend.domain.usecase.book.UploadCoverUseCase;
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
    public PagedModel<BookResponse> getAll(Pageable pageable) {
        PageQuery query = new PageQuery(
            pageable.getPageNumber(),
            pageable.getPageSize()
        );
        PageResult<Book> books = getBooksUseCase.execute(query);
        PageResult<BookResponse> bookResponses = books.map(bookApiMapper::toResponse);
        Page<BookResponse> page = new PageImpl<>(
            bookResponses.content(),
            PageRequest.of(bookResponses.number(), bookResponses.size()),
            bookResponses.totalElements()
        );
        return new PagedModel<>(page);
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
