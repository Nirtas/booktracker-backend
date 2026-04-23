package ru.jerael.booktracker.backend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedModel;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.jerael.booktracker.backend.domain.constant.BookRules;
import ru.jerael.booktracker.backend.domain.constant.PaginationRules;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import ru.jerael.booktracker.backend.domain.model.book.UploadCoverPayload;
import ru.jerael.booktracker.backend.domain.model.image.ImageFile;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.model.pagination.SortDirection;
import ru.jerael.booktracker.backend.domain.usecase.book.*;
import ru.jerael.booktracker.backend.web.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.web.dto.book.BookDetailsUpdateRequest;
import ru.jerael.booktracker.backend.web.dto.book.BookResponse;
import ru.jerael.booktracker.backend.web.mapper.BookWebMapper;
import ru.jerael.booktracker.backend.web.mapper.UploadCoverWebMapper;
import ru.jerael.booktracker.backend.web.validator.FileValidator;
import java.time.Duration;
import java.util.UUID;

@Tag(name = "Books")
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
    private final GetBookCoverUseCase getBookCoverUseCase;

    private final FileValidator fileValidator;
    private final BookWebMapper bookWebMapper;
    private final UploadCoverWebMapper uploadCoverWebMapper;

    @Operation(summary = "Get all books")
    @GetMapping
    public PagedModel<BookResponse> getAll(@ParameterObject Pageable pageable, @AuthenticationPrincipal UUID userId) {
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
        PageResult<Book> books = getBooksUseCase.execute(query, userId);
        PageResult<BookResponse> bookResponses = books.map(bookWebMapper::toResponse);
        Page<BookResponse> page = new PageImpl<>(
            bookResponses.content(),
            PageRequest.of(bookResponses.number(), bookResponses.size()),
            bookResponses.totalElements()
        );
        return new PagedModel<>(page);
    }

    @Operation(summary = "Get book by id")
    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
        Book book = getBookByIdUseCase.execute(id, userId);
        return bookWebMapper.toResponse(book);
    }

    @Operation(summary = "Delete book by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
        deleteBookUseCase.execute(id, userId);
    }

    @Operation(summary = "Update book details")
    @PatchMapping("/{id}")
    public BookResponse updateDetails(
        @PathVariable UUID id,
        @Valid @RequestBody BookDetailsUpdateRequest request,
        @AuthenticationPrincipal UUID userId
    ) {
        BookDetailsUpdate data = bookWebMapper.toDomain(request, id, userId);
        Book book = updateBookDetailsUseCase.execute(data);
        return bookWebMapper.toResponse(book);
    }

    @Operation(summary = "Create book")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody BookCreationRequest request, @AuthenticationPrincipal UUID userId) {
        BookCreation data = bookWebMapper.toDomain(request, userId);
        Book book = createBookUseCase.execute(data);
        return bookWebMapper.toResponse(book);
    }

    @Operation(summary = "Upload book cover")
    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BookResponse uploadCover(
        @PathVariable UUID id,
        @RequestParam(BookRules.COVER_FIELD_NAME) MultipartFile file,
        @AuthenticationPrincipal UUID userId
    ) {
        fileValidator.validate(file, BookRules.COVER_FIELD_NAME);
        UploadCoverPayload data = uploadCoverWebMapper.toDomain(file, id, userId);
        Book book = uploadCoverUseCase.execute(data);
        return bookWebMapper.toResponse(book);
    }

    @Operation(summary = "Delete book cover")
    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCover(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
        deleteCoverUseCase.execute(id, userId);
    }

    @Operation(summary = "Get book cover")
    @GetMapping("/{id}/cover")
    public ResponseEntity<Resource> getCover(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
        ImageFile imageFile = getBookCoverUseCase.execute(id, userId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + imageFile.fileName() + "\"")
            .contentType(MediaType.parseMediaType(imageFile.contentType()))
            .contentLength(imageFile.size())
            .cacheControl(CacheControl.maxAge(Duration.ofHours(24)))
            .body(new InputStreamResource(imageFile.content()));
    }
}
