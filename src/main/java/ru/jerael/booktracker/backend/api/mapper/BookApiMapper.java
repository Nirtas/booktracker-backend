package ru.jerael.booktracker.backend.api.mapper;

import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.api.dto.book.BookCreationRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookDetailsUpdateRequest;
import ru.jerael.booktracker.backend.api.dto.book.BookResponse;
import ru.jerael.booktracker.backend.domain.model.book.Book;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import ru.jerael.booktracker.backend.domain.model.book.BookStatus;
import java.util.List;

@Component
public class BookApiMapper {
    private final GenreApiMapper genreApiMapper;

    public BookApiMapper(GenreApiMapper genreApiMapper) {
        this.genreApiMapper = genreApiMapper;
    }

    public BookResponse toResponse(Book book) {
        if (book == null) return null;

        return new BookResponse(
            book.id(),
            book.title(),
            book.author(),
            book.coverFileName(), // TODO: generate a full url
            book.status().getValue(),
            book.createdAt(),
            genreApiMapper.toResponses(book.genres())
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
