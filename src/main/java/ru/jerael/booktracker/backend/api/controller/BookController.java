package ru.jerael.booktracker.backend.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.jerael.booktracker.backend.api.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookDetailsUpdateRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookResponse;
import ru.jerael.booktracker.backend.api.mapper.BookApiMapper;
import ru.jerael.booktracker.backend.api.mapper.UploadCoverApiMapper;
import ru.jerael.booktracker.backend.api.validator.FileValidator;
import ru.jerael.booktracker.backend.domain.constant.PaginationRules;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import ru.jerael.booktracker.backend.domain.model.book.UploadCover;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.model.pagination.SortDirection;
import ru.jerael.booktracker.backend.domain.usecase.book.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {
    private final GetBooksUseCase getBooksUseCase;
    private final GetBookByIdUseCase getBookByIdUseCase;
    private final CreateBookUseCase createBookUseCase;
    private final UploadCoverUseCase uploadCoverUseCase;
    private final DeleteCoverUseCase deleteCoverUseCase;
    private final DeleteBookUseCase deleteBookUseCase;
    private final UpdateBookDetailsUseCase updateBookDetailsUseCase;

    private final FileValidator fileValidator;
    private final BookApiMapper bookApiMapper;
    private final UploadCoverApiMapper uploadCoverApiMapper;

    @GetMapping
    public PagedModel<BookResponse> getAll(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream().findFirst().orElse(null);
        String sortBy = order != null ? order.getProperty() : PaginationRules.DEFAULT_SORT_FIELD;
        SortDirection direction = order != null && order.isAscending()
            ? SortDirection.ASC
            : SortDirection.DESC;

        PageQuery query = new PageQuery(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            sortBy,
            direction
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable UUID id) {
        deleteBookUseCase.execute(id);
    }

    @PatchMapping("/{id}")
    public BookResponse updateDetails(@PathVariable UUID id, @Valid @RequestBody BookDetailsUpdateRequest request) {
        BookDetailsUpdate data = bookApiMapper.toDomain(request);
        Book book = updateBookDetailsUseCase.execute(id, data);
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
        UploadCover data = uploadCoverApiMapper.toDomain(file);
        Book book = uploadCoverUseCase.execute(id, data);
        return bookApiMapper.toResponse(book);
    }

    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCover(@PathVariable UUID id) {
        deleteCoverUseCase.execute(id);
    }
}
