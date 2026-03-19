package ru.jerael.booktracker.backend.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import ru.jerael.booktracker.backend.web.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.web.dto.book.BookDetailsUpdateRequest;
import ru.jerael.booktracker.backend.web.dto.book.BookResponse;
import ru.jerael.booktracker.backend.web.util.LinkBuilder;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookWebMapper {
    private final GenreWebMapper genreWebMapper;
    private final AuthorWebMapper authorWebMapper;
    private final PublisherWebMapper publisherWebMapper;
    private final LanguageWebMapper languageWebMapper;
    private final ReadingAttemptWebMapper readingAttemptWebMapper;
    private final NoteWebMapper noteWebMapper;
    private final LinkBuilder linkBuilder;

    public BookResponse toResponse(Book book) {
        if (book == null) return null;

        String coverUrl = null;
        if (book.coverFileName() != null && !book.coverFileName().isBlank()) {
            coverUrl = linkBuilder.buildCoverUrl(book.id());
        }

        return new BookResponse(
            book.id(),
            book.title(),
            book.author(),
            coverUrl,
            book.status().getValue(),
            book.createdAt(),
            genreWebMapper.toResponses(book.genres()),
            book.authors().stream().map(authorWebMapper::toResponse).collect(Collectors.toSet()),
            book.description(),
            publisherWebMapper.toResponse(book.publisher()),
            languageWebMapper.toResponse(book.language()),
            book.publishedOn(),
            book.totalPages(),
            book.isbn10(),
            book.isbn13(),
            book.attempts().stream().map(readingAttemptWebMapper::toResponse).toList(),
            book.notes().stream().map(noteWebMapper::toResponse).toList()
        );
    }

    public List<BookResponse> toResponses(List<Book> books) {
        if (books == null) return List.of();

        return books.stream().map(this::toResponse).toList();
    }

    public BookCreation toDomain(BookCreationRequest request) {
        if (request == null) return null;

        return new BookCreation(
            request.title().trim(),
            request.author().trim(),
            BookStatus.fromString(request.status()),
            request.genreIds()
        );
    }

    public BookDetailsUpdate toDomain(BookDetailsUpdateRequest request) {
        if (request == null) return null;

        return new BookDetailsUpdate(
            request.title(),
            request.author(),
            BookStatus.fromString(request.status()),
            request.genreIds()
        );
    }
}
