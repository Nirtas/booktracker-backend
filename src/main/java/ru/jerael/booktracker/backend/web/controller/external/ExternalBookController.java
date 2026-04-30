package ru.jerael.booktracker.backend.web.controller.external;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.jerael.booktracker.backend.domain.model.book.BookMetadata;
import ru.jerael.booktracker.backend.domain.model.book.BookSearchQuery;
import ru.jerael.booktracker.backend.domain.usecase.external.book.GetBookMetadataUseCase;
import ru.jerael.booktracker.backend.web.dto.book.BookMetadataResponse;
import ru.jerael.booktracker.backend.web.mapper.BookWebMapper;

@Tag(name = "External Books")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/books")
public class ExternalBookController {
    private final GetBookMetadataUseCase getBookMetadataUseCase;
    private final BookWebMapper bookWebMapper;

    @Operation(summary = "Find book metadata in external APIs")
    @GetMapping()
    public BookMetadataResponse findBookMetadata(@RequestParam String isbn) {
        BookSearchQuery query = new BookSearchQuery(isbn);
        BookMetadata result = getBookMetadataUseCase.execute(query);
        return bookWebMapper.toResponse(result);
    }
}
